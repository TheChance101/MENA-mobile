package net.thechance.mena.faith.presentation.feature.prayertime

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import net.thechance.mena.faith.domain.repository.PrayerTimeRepository
import net.thechance.mena.faith.domain.service.PrayerTimeService
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.utils.extentions.prayerTime.formatCountdown
import net.thechance.mena.faith.presentation.utils.extentions.prayerTime.getHijriReadableDate
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.service.LocationService
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class PrayerTimeViewModel(
    private val prayerTimeRepository: PrayerTimeRepository,
    private val locationService: LocationService,
    private val prayerTimeService: PrayerTimeService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<PrayerTimeUiState, PrayerTimeEffect>(PrayerTimeUiState()),
    PrayerTimeInteractionListener {

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
        updateState { state -> state.copy(address = address.addressLine) }

        tryToExecute(
            execute = {
                prayerTimeRepository.getPrayerTimes(
                    date = Clock.System.now(),
                    address = address
                )
            },
            onSuccess = { onPrayerTimesSuccess(prayerTimes = it, address = address) },
            dispatcher = dispatcher
        )
    }

    private fun onPrayerTimesSuccess(prayerTimes: List<PrayerTime>, address: Address) {
        tryToExecute(
            execute = {
                val filteredPrayerTimes = prayerTimes.filter { it.name != PrayerName.SUNRISE }
                val hijriDate = getHijriReadableDate(prayerTimes)
                updateState {
                    it.copy(
                        prayerTimes = filteredPrayerTimes,
                        currentDate = hijriDate
                    )
                }
            },
            onSuccess = { startNextPrayerObserver(address) })
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

    override fun onPrevDateClick() = sendEffect(PrayerTimeEffect.NavigatePrevDate)

    override fun onNextDateClick() = sendEffect(PrayerTimeEffect.NavigateNextDate)

    override fun onDateDropdownClick() = sendEffect(PrayerTimeEffect.NavigateCalenderDialog)

    override fun onLocationClick() = sendEffect(PrayerTimeEffect.NavigateToAddressesScreen)
}