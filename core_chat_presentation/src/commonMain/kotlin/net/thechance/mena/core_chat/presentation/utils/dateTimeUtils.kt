@file:OptIn(ExperimentalTime::class)

package net.thechance.mena.core_chat.presentation.utils

import androidx.compose.runtime.Composable
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.hour_ago
import mena.core_chat_presentation.generated.resources.hours_ago
import mena.core_chat_presentation.generated.resources.just_now
import mena.core_chat_presentation.generated.resources.minute_ago
import mena.core_chat_presentation.generated.resources.minutes_ago
import mena.core_chat_presentation.generated.resources.more_hours_ago
import mena.core_chat_presentation.generated.resources.more_minutes_ago
import mena.core_chat_presentation.generated.resources.two_hours_ago
import mena.core_chat_presentation.generated.resources.two_minutes_ago
import mena.core_chat_presentation.generated.resources.yesterday
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun LocalDateTime.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime {
    return Clock.System.now().toLocalDateTime(timeZone)
}

fun LocalDate.minusDays(days: Int): LocalDate {
    return this.minus(DatePeriod(days = days))
}

fun LocalDateTime.formatAsTime(am: String, pm: String): String {
    val timeFormat = LocalDateTime.Format {
        amPmHour()
        char(':')
        minute()
        char(' ')
        amPmMarker(am, pm)
    }
    return this.format(timeFormat)
}

fun LocalDate.format(pattern: String = "dd-MM-yyyy"): String {
    return pattern
        .replace("dd", day.toString().padStart(2, '0'))
        .replace("MM", month.ordinal.plus(1).toString().padStart(2, '0'))
        .replace("yyyy", year.toString())
}

@Composable
fun LocalDateTime.formatAsPastDateTime(
    am: String,
    pm: String,
): String {
    val currentDate = LocalDateTime.now().date
    if (this.date == currentDate) {
        return this.time.toHoursMinutesAgo()
    }

    val timeFormat = LocalDateTime.Format {
        amPmHour()
        char(':')
        minute()
        char(' ')
        amPmMarker(am, pm)
        char(' ')
        day()
        char('-')
        monthNumber()
        char('-')
        year()
    }

    return this.format(timeFormat)
}

@Composable
fun LocalTime.toHoursMinutesAgo(): String {
    val currentTime = LocalDateTime.now().time
    val hours = currentTime.hour - this.hour
    return when {
        hours <= 0 -> {
            val minutes = currentTime.minute - this.minute
            when {
                minutes <= 0 -> UiText.StringRes(Res.string.just_now)
                minutes == 1 -> UiText.StringRes(Res.string.minute_ago)
                minutes == 2 -> UiText.StringRes(Res.string.two_minutes_ago)
                minutes in 3..10 -> UiText.StringRes(Res.string.minutes_ago, minutes)
                else -> UiText.StringRes(Res.string.more_minutes_ago, minutes)
            }
        }

        hours == 1 -> UiText.StringRes(Res.string.hour_ago)
        hours == 2 -> UiText.StringRes(Res.string.two_hours_ago)
        hours in 3..10 -> UiText.StringRes(Res.string.hours_ago, hours)
        else -> UiText.StringRes(Res.string.more_hours_ago, hours)
    }.asString()
}

fun getFormattedTimeWithTodayTimeOrYesterdayTextOrSimpleDate(
    dateTime: LocalDateTime,
    am: String,
    pm: String,
): UiText {
    val now = LocalDateTime.now()
    return when (dateTime.date) {
        now.date -> UiText.DynamicString(dateTime.formatAsTime(am, pm))
        now.date.minusDays(1) -> UiText.StringRes(Res.string.yesterday)
        else -> UiText.DynamicString(dateTime.date.format())
    }
}