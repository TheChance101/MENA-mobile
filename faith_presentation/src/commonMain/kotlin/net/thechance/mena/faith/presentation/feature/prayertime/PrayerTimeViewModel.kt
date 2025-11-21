package net.thechance.mena.faith.presentation.feature.prayertime

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import net.thechance.mena.faith.domain.repository.PrayerTimeRepository
import net.thechance.mena.faith.domain.service.PrayerTimeService
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.utils.IslamicDate
import net.thechance.mena.faith.presentation.utils.IslamicDateCalculator
import net.thechance.mena.faith.presentation.utils.extentions.prayerTime.calculateNextIslamicDate
import net.thechance.mena.faith.presentation.utils.extentions.prayerTime.calculatePreviousIslamicDate
import net.thechance.mena.faith.presentation.utils.extentions.prayerTime.formatCountdown
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.service.LocationService
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class PrayerTimeViewModel(
    private val prayerTimeRepository: PrayerTimeRepository,
    private val locationService: LocationService,
    private val prayerTimeService: PrayerTimeService,
    private val islamicDateCalculator: IslamicDateCalculator,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<PrayerTimeUiState, PrayerTimeEffect>(PrayerTimeUiState()),
    PrayerTimeInteractionListener {

    private var currentAddress: Address? = null

    init {
        getUserLocation()
    }

    private fun getUserLocation() {
        tryToExecute(
            execute = { locationService.getActiveAddress()!! },
            onSuccess = ::onGetUserLocationSuccess,
            onError = { sendEffect(PrayerTimeEffect.NavigateToAddressesScreen) },
            dispatcher = dispatcher
        )
    }

    private fun onGetUserLocationSuccess(address: Address) {
        currentAddress = address
        updateState { it.copy(address = address.addressLine) }
        getPrayerTimes(address, Clock.System.now())
    }

    private fun getPrayerTimes(address: Address, date: Instant) {
        tryToExecute(
            execute = { prayerTimeRepository.getPrayerTimes(date = date, address = address) },
            onSuccess = { onPrayerTimesSuccess(prayerTimes = it, address = address) },
            dispatcher = dispatcher
        )
    }

    private fun onPrayerTimesSuccess(prayerTimes: List<PrayerTime>, address: Address) {
        tryToExecute(
            execute = {
                val filteredPrayerTimes = prayerTimes.filter { it.name != PrayerName.SUNRISE }

                val currentIslamicDate = IslamicDate.now(islamicDateCalculator)

                updateState {
                    it.copy(
                        prayerTimes = filteredPrayerTimes,
                        currentDate = currentIslamicDate,
                    )
                }
            },
            onSuccess = {
                startNextPrayerObserver(address)
            }
        )
    }

    private fun startNextPrayerObserver(address: Address) {
        tryToCollect(
            block = { prayerTimeService.getNextPrayer(address) },
            onEmitNewValue =
                { nextPrayer ->
                    nextPrayer?.let {
                        startCountdownTimer(nextPrayer = it, address = address)
                    } ?: run {
                        updateState { state ->
                            state.copy(
                                nextPrayerName = null,
                                nextPrayerCountdown = ""
                            )
                        }
                    }
                },
            dispatcher = dispatcher
        )
    }

    private fun startCountdownTimer(nextPrayer: PrayerTime, address: Address) {
        val currentTime = Clock.System.now()
        val remainingMillis =
            nextPrayer.time.toEpochMilliseconds() - currentTime.toEpochMilliseconds()
        tryToExecute(
            execute = {
                updateState { state ->
                    state.copy(
                        nextPrayerName = nextPrayer.name,
                        nextPrayerCountdown = formatCountdown(remainingMillis)
                    )
                }
            },
            onSuccess = { startNextPrayerObserver(address) })
    }

    override fun onBackClick() = sendEffect(PrayerTimeEffect.NavigateBack)

    override fun onPrevDateClick() {
        val currentDate = uiState.value.currentDate
        val previousIslamicDate = calculatePreviousIslamicDate(currentDate)

        updateState { it.copy(currentDate = previousIslamicDate) }

        getPrayerTimesForIslamicDate(previousIslamicDate)
    }

    override fun onNextDateClick() {
        val currentDate = uiState.value.currentDate
        val nextIslamicDate = calculateNextIslamicDate(currentDate)

        updateState { it.copy(currentDate = nextIslamicDate) }

        getPrayerTimesForIslamicDate(nextIslamicDate)
    }

    override fun onDateDropdownClick() =
        updateState {
            it.copy(
                isDatePickerShown = true,
                islamicDatePickerUiState = PrayerTimeUiState.IslamicDatePickerUiState(
                    selectedIslamicDate = uiState.value.currentDate,
                )
            )
        }

    override fun onLocationClick() = sendEffect(PrayerTimeEffect.NavigateToAddressesScreen)

    override fun onSelectedDateChange(day: Int, month: Int, year: Int) {
        val selectedIslamicDate = IslamicDate(day, month, year)

        updateState {
            it.copy(
                islamicDatePickerUiState = it.islamicDatePickerUiState.copy(
                    selectedIslamicDate = selectedIslamicDate,
                    isClearDateActive = selectedIslamicDate != IslamicDate.now(islamicDateCalculator),
                )
            )
        }
    }

    override fun onDateSelected() {
        val selectedIslamicDate = uiState.value.islamicDatePickerUiState.selectedIslamicDate
        updateState {
            it.copy(
                isDatePickerShown = false,
                currentDate = selectedIslamicDate,
                isTodayPrayer = selectedIslamicDate == IslamicDate.now(islamicDateCalculator),
                islamicDatePickerUiState = PrayerTimeUiState.IslamicDatePickerUiState(),
            )
        }

        getPrayerTimesForIslamicDate(uiState.value.currentDate)
    }

    override fun onClearSelectedDate() =
        updateState {
            it.copy(
                islamicDatePickerUiState = it.islamicDatePickerUiState.copy(
                    selectedIslamicDate = IslamicDate.now(islamicDateCalculator),
                    isClearDateActive = false,
                )
            )
        }

    override fun onDatePickerDismiss() =
        updateState {
            it.copy(
                isDatePickerShown = false,
                islamicDatePickerUiState = PrayerTimeUiState.IslamicDatePickerUiState(),
            )
        }

    private fun getPrayerTimesForIslamicDate(islamicDate: IslamicDate) {
        val address = currentAddress ?: return

        tryToExecute(
            execute = {
                prayerTimeRepository.getPrayerTimesByHijriDate(
                    date = formatIslamicDate(islamicDate),
                    isHijri = true,
                    address = address
                )
            },
            onSuccess = ::onHijriPrayerTimesSuccess,
            dispatcher = dispatcher
        )
    }

    private fun onHijriPrayerTimesSuccess(prayerTimes: List<PrayerTime>) {
        val filteredPrayerTimes = prayerTimes.filter { it.name != PrayerName.SUNRISE }
        updateState { it.copy(prayerTimes = filteredPrayerTimes) }
    }

    private fun formatIslamicDate(islamicDate: IslamicDate): String =
        "${islamicDate.year}-${islamicDate.month}-${islamicDate.day}"
}
