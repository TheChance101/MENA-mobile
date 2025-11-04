package net.thechance.mena.faith.presentation.feature.prayertime

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import net.thechance.mena.faith.domain.repository.PrayerTimeRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.utils.extentions.prayerTime.findNextPrayer
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
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<PrayerTimeUiState, PrayerTimeEffect>(PrayerTimeUiState()),
    PrayerTimeInteractionListener {

    init {
        loadTodayPrayerTimes()
        updateNextPrayerInfo()
    }

    override fun onBackClick() = sendEffect(PrayerTimeEffect.NavigateBack)
    override fun onPrevDateClick() = sendEffect(PrayerTimeEffect.NavigatePrevDate)
    override fun onNextDateClick() = sendEffect(PrayerTimeEffect.NavigateNextDate)
    override fun onDateDropdownClick() = sendEffect(PrayerTimeEffect.NavigateCalenderDialog)
    override fun onChangeLocation() = handleInvalidAddress()

    private fun loadTodayPrayerTimes() {
        tryToExecute(
            dispatcher = dispatcher,
            execute = {
                val address = getValidatedAddress() ?: return@tryToExecute null
                prayerTimeRepository.getPrayerTimes(date = Clock.System.now(), address = address)
            },
            onSuccess = { prayerTimes -> prayerTimes?.let(::onPrayerTimesSuccess) },
        )
    }

    private suspend fun getValidatedAddress(): Address? {
        val address = locationService.getActiveAddress()

        if (address == null || address.addressLine.isEmpty()) {
            handleInvalidAddress()
            return null
        }

        updateState { state -> state.copy(address = address.addressLine) }
        return address
    }

    private fun onPrayerTimesSuccess(prayerTimes: List<PrayerTime>) {
        val filteredPrayerTimes = prayerTimes.filter { it.name != PrayerName.SUNRISE }
        val hijriDate = getHijriReadableDate(prayerTimes)

        updateState {
            it.copy(
                prayerTimes = filteredPrayerTimes.map { prayer ->
                    prayer.copy(time = prayer.time)
                },
                currentDate = hijriDate
            )
        }
    }

    private fun updateNextPrayerInfo() {
        val prayerTimes = uiState.value.prayerTimes
        val currentTime = Clock.System.now()
        val nextPrayer = findNextPrayer(prayerTimes, currentTime)

        nextPrayer?.let { prayer ->
            val remainingMillis =
                prayer.time.toEpochMilliseconds() - currentTime.toEpochMilliseconds()

            if (remainingMillis > 0) {
                updateState { state ->
                    state.copy(
                        nextPrayerName = prayer.name,
                        nextPrayerCountdown = formatCountdown(remainingMillis)
                    )
                }
            } else {
                updateState { state ->
                    state.copy(
                        nextPrayerName = prayerTimes.firstOrNull()?.name ?: PrayerName.DHUHR,
                        nextPrayerCountdown = "--:--:--"
                    )
                }
            }
        }
    }

    private fun handleInvalidAddress() = sendEffect(PrayerTimeEffect.NavigateToAddressesScreen)
}