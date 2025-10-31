package net.thechance.mena.faith.presentation.feature.prayertime

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.thechance.mena.faith.domain.entity.Location
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import net.thechance.mena.faith.domain.repository.PrayerTimeRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.utils.extentions.prayerTime.getHijriReadableDate
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class PrayerTimeViewModel(
    private val prayerTimeRepository: PrayerTimeRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<PrayerTimeUiState, PrayerTimeEffect>(PrayerTimeUiState()),
    PrayerTimeInteractionListener {

    private val defaultLocation = Location(latitude = 30.186173, longitude = 31.158446)
    private val months = listOf(
        "Muharram", "Safar", "Rabi' al-awwal", "Rabi' al-thani",
        "Jumada al-awwal", "Jumada al-thani", "Rajab", "Sha'ban",
        "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhul-Hijjah"
    )

    init {
        loadTodayPrayerTimes()
        startCountdownTimer()
    }

    override fun onBackClick() = sendEffect(PrayerTimeEffect.NavigateBack)

    override fun onPrevDateClick() {
        val currentDate = uiState.value.currentDate
        val newDate = getPreviousHijriDate(currentDate)
        updateState { it.copy(currentDate = newDate) }
        updatePrayerTimes(date = newDate)
    }

    override fun onNextDateClick() {
        val currentDate = uiState.value.currentDate
        val newDate = getNextHijriDate(currentDate)
        updateState { it.copy(currentDate = newDate) }
        updatePrayerTimes(date = newDate)
    }

    override fun onDateDropdownClick() {
        sendEffect(PrayerTimeEffect.NavigateCalenderBottomSheet)
    }

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

    private fun updatePrayerTimes(date: String) {
        tryToExecute(
            dispatcher = dispatcher,
            execute = {
                prayerTimeRepository.getPrayerTimeInHijriDate(
                    date = date,
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
                    prayer.copy(
                        // Format the time for display
                        time = prayer.time
                    )
                },
                currentDate = hijriDate
            )
        }
        updateNextPrayerInfo()
    }

    private fun updateNextPrayerInfo() {
        val prayerTimes = uiState.value.prayerTimes
        val currentTime = Clock.System.now()

        // Find next prayer
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
                // If no upcoming prayer today, show first prayer of next day
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
        // Sort prayers by time
        val sortedPrayers = prayerTimes.sortedBy { it.time }

        // Find first prayer that is after current time
        val nextPrayer = sortedPrayers.firstOrNull { it.time > currentTime }

        return nextPrayer
            ?: sortedPrayers.firstOrNull() // Return first prayer if none found (for next day)
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
                delay(1.seconds)
                updateNextPrayerInfo()
            }
        }
    }

    private fun getPreviousHijriDate(currentDate: String): String {
        val parts = currentDate.replace("H", "").trim().split(" ")
        if (parts.size != 3) return currentDate

        val day = parts[0].toIntOrNull() ?: return currentDate
        val month = parts[1]
        val year = parts[2].toIntOrNull() ?: return currentDate

        val previousDay = if (day > 1) {
            day - 1
        } else {
            val previousMonth = getPreviousMonth(month)
            val daysInPreviousMonth = getDaysInHijriMonth(previousMonth, year)
            if (previousMonth == "Dhul-Hijjah") {
                return formatHijriDate(daysInPreviousMonth, "Dhul-Hijjah", year - 1)
            }
            daysInPreviousMonth
        }

        val newMonth = if (day == 1) getPreviousMonth(month) else month
        val newYear = if (day == 1 && month == "Muharram") year - 1 else year

        return formatHijriDate(previousDay, newMonth, newYear)
    }

    private fun getNextHijriDate(currentDate: String): String {
        val parts = currentDate.replace("H", "").trim().split(" ")
        if (parts.size != 3) return currentDate

        val day = parts[0].toIntOrNull() ?: return currentDate
        val month = parts[1]
        val year = parts[2].toIntOrNull() ?: return currentDate

        val daysInCurrentMonth = getDaysInHijriMonth(month, year)

        val nextDay = if (day < daysInCurrentMonth) {
            day + 1
        } else {
            1
        }

        val newMonth = if (day == daysInCurrentMonth) getNextMonth(month) else month
        val newYear = if (day == daysInCurrentMonth && month == "Dhul-Hijjah") year + 1 else year

        return formatHijriDate(nextDay, newMonth, newYear)
    }

    private fun getPreviousMonth(month: String): String {
        val index = months.indexOf(month)
        return if (index > 0) months[index - 1] else months[11]
    }

    private fun getNextMonth(month: String): String {
        val index = months.indexOf(month)
        return if (index < 11) months[index + 1] else months[0]
    }

    private fun getDaysInHijriMonth(month: String, year: Int): Int {
        val monthIndex = months.indexOf(month)

        return when (monthIndex) {
            0, 2, 4, 6, 8, 10 -> 30 // Odd months: 30 days
            else -> {
                if (monthIndex == 11 && isHijriLeapYear(year)) 30 else 29
            }
        }
    }

    private fun isHijriLeapYear(year: Int): Boolean {
        val leapYearsInCycle = listOf(2, 5, 7, 10, 13, 16, 18, 21, 24, 26, 29)
        val cycleYear = year % 30
        return cycleYear in leapYearsInCycle
    }

    private fun formatHijriDate(day: Int, month: String, year: Int): String {
        return "$day $month ${year}H"
    }
}