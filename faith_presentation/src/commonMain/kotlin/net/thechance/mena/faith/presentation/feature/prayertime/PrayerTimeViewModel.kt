package net.thechance.mena.faith.presentation.feature.prayertime

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import net.thechance.mena.faith.domain.repository.PrayerTimeRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.utils.extentions.prayerTime.formatCountdown
import net.thechance.mena.faith.presentation.utils.extentions.prayerTime.getHijriReadableDate
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.service.LocationService
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class PrayerTimeViewModel(
    private val prayerTimeRepository: PrayerTimeRepository,
    private val locationService: LocationService,
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
            onSuccess = ::onPrayerTimesSuccess,
            dispatcher = dispatcher
        )
    }

    private fun onPrayerTimesSuccess(prayerTimes: List<PrayerTime>) {
        val filteredPrayerTimes = prayerTimes.filter { it.name != PrayerName.SUNRISE }
        val hijriDate = getHijriReadableDate(prayerTimes)

        updateState {
            it.copy(
                prayerTimes = filteredPrayerTimes,
                currentDate = hijriDate
            )
        }
        updateNextPrayerInfo()
        startCountdownTimer()
    }

    private fun updateNextPrayerInfo() {
        val prayerTimes = uiState.value.prayerTimes

        if (prayerTimes.isEmpty()) return


        val currentTime = Clock.System.now()
        val nextPrayer = findNextPrayer(prayerTimes, currentTime)

        if (nextPrayer != null) updateStateWithNextPrayer(nextPrayer, currentTime)
        else handleTomorrowFirstPrayer(prayerTimes, currentTime)
    }

    private fun findNextPrayer(prayerTimes: List<PrayerTime>, currentTime: Instant): PrayerTime? {
        return prayerTimes.firstOrNull {
            it.time.toEpochMilliseconds() > currentTime.toEpochMilliseconds()
        }
    }

    private fun updateStateWithNextPrayer(nextPrayer: PrayerTime, currentTime: Instant) {
        val remainingMillis = calculateRemainingTime(nextPrayer.time, currentTime)

        updateState { state ->
            state.copy(
                nextPrayerName = nextPrayer.name,
                nextPrayerCountdown = formatCountdown(remainingMillis)
            )
        }
    }

    private fun handleTomorrowFirstPrayer(prayerTimes: List<PrayerTime>, currentTime: Instant) {
        val firstPrayer = prayerTimes.firstOrNull() ?: return

        val tomorrowFirstPrayerTime = calculateNextFajrTime(firstPrayer.time)
        val remainingMillis = calculateRemainingTime(tomorrowFirstPrayerTime, currentTime)

        updateState { state ->
            state.copy(
                nextPrayerName = firstPrayer.name,
                nextPrayerCountdown = formatCountdown(remainingMillis)
            )
        }
    }

    private fun calculateRemainingTime(prayerTime: Instant, currentTime: Instant): Long {
        return prayerTime.toEpochMilliseconds() - currentTime.toEpochMilliseconds()
    }

    private fun calculateNextFajrTime(todayPrayerTime: Instant): Instant {
        val oneDayInMillis = ONE_DAY_IN_MILLIS
        return Instant.fromEpochMilliseconds(todayPrayerTime.toEpochMilliseconds() + oneDayInMillis)
    }

    private fun startCountdownTimer() {
        viewModelScope.launch(dispatcher) {
            while (true) {
                delay(COUNTDOWN_UPDATE_INTERVAL)
                updateNextPrayerInfo()
            }
        }
    }

    override fun onBackClick() = sendEffect(PrayerTimeEffect.NavigateBack)

    override fun onPrevDateClick() = sendEffect(PrayerTimeEffect.NavigatePrevDate)

    override fun onNextDateClick() = sendEffect(PrayerTimeEffect.NavigateNextDate)

    override fun onDateDropdownClick() = sendEffect(PrayerTimeEffect.NavigateCalenderDialog)

    override fun onLocationClick() = sendEffect(PrayerTimeEffect.NavigateToAddressesScreen)

    private companion object {
        const val ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000L
        const val COUNTDOWN_UPDATE_INTERVAL = 1000L
    }
}