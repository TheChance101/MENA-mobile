package net.thechance.mena.faith.presentation.feature.prayertime

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import net.thechance.mena.faith.domain.entity.Location
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import net.thechance.mena.faith.domain.repository.PrayerTimeRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.utils.extentions.prayerTime.getHijriReadableDate
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class PrayerTimeViewModel(
    private val prayerTimeRepository: PrayerTimeRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<PrayerTimeUiState, PrayerTimeEffect>(PrayerTimeUiState()),
    PrayerTimeInteractionListener {

    private val defaultLocation = Location(latitude = 30.186173, longitude = 31.158446)

    init {
        loadTodayPrayerTimes()
        startCountdownTimer()
    }

    override fun onBackClick() = sendEffect(PrayerTimeEffect.NavigateBack)

    override fun onPrevDateClick() = sendEffect(PrayerTimeEffect.NavigatePrevDate)

    override fun onNextDateClick() = sendEffect(PrayerTimeEffect.NavigateNextDate)

    override fun onDateDropdownClick() = sendEffect(PrayerTimeEffect.NavigateCalenderBottomSheet)

    private fun loadTodayPrayerTimes() {
        tryToExecute(
            dispatcher = dispatcher,
            execute = {
                prayerTimeRepository.getPrayerTimes(
                    date = Clock.System.now(),
                    location = defaultLocation
                )
            },
            onSuccess = ::onPrayerTimesSuccess,
        )
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
                val countdown = formatCountdown(remainingMillis)

                updateState { state ->
                    state.copy(
                        nextPrayerName = prayer.name,
                        nextPrayerCountdown = countdown
                    )
                }
            } else {
                val firstPrayer = prayerTimes.firstOrNull()
                firstPrayer?.let {
                    updateState { state ->
                        state.copy(
                            nextPrayerName = it.name,
                            nextPrayerCountdown = "--:--:--"
                        )
                    }
                }
            }
        }
    }

    private fun findNextPrayer(
        prayerTimes: List<PrayerTime>,
        currentTime: kotlin.time.Instant
    ): PrayerTime? {
        val sortedPrayers = prayerTimes.sortedBy { it.time }
        val nextPrayer = sortedPrayers.firstOrNull { it.time > currentTime }

        return nextPrayer ?: sortedPrayers.firstOrNull()
    }

    private fun formatCountdown(remainingMillis: Long): String {
        val remainingSeconds = remainingMillis / 1000

        val hours = remainingSeconds / 3600
        val minutes = (remainingSeconds % 3600) / 60
        val seconds = remainingSeconds % 60

        return "${hours.toString().padStart(2, '0')}:${
            minutes.toString().padStart(2, '0')
        }:${seconds.toString().padStart(2, '0')}"
    }

    private fun startCountdownTimer() {
        viewModelScope.launch(dispatcher) {
            while (true) {
                updateNextPrayerInfo()
            }
        }
    }
}