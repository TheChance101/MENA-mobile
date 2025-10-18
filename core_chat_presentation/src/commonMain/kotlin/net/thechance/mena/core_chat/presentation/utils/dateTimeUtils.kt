@file:OptIn(ExperimentalTime::class)

package net.thechance.mena.core_chat.presentation.utils

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun LocalDateTime.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime {
    return Clock.System.now().toLocalDateTime(timeZone)
}

fun LocalDate.minusDays(days: Int): LocalDate {
    return this.minus(DatePeriod(days = days))
}

fun LocalDateTime.formatAsTime(): String {
    val timeFormat = LocalDateTime.Format {
        amPmHour()
        char(':')
        minute()
        amPmMarker("AM", "PM") // adds AM/PM
    }

    return this.format(timeFormat)
}

fun LocalDate.format(pattern: String = "dd-MM-yyyy"): String {
    return pattern
        .replace("dd", day.toString().padStart(2, '0'))
        .replace("MM", month.ordinal.plus(1).toString().padStart(2, '0'))
        .replace("yyyy", year.toString())
}

fun LocalDateTime.formatAsPastDateTime(): String {

    val currentDate = LocalDateTime.now().date
    if (this.date == currentDate) {
        return this.time.toHoursMinutesAgo()
    }

    val timeFormat = LocalDateTime.Format {
        amPmHour()
        char(':')
        minute()
        amPmMarker("AM", "PM")
        char(' ')
        day()
        char('-')
        monthNumber()
        char('-')
        year()
    }

    return this.format(timeFormat)
}

fun LocalTime.toHoursMinutesAgo(): String {
    val currentTime = LocalDateTime.now().time
    val hoursAgo = currentTime.hour - this.hour
    return if (hoursAgo <= 0) {
        val minutesAgo = currentTime.minute - this.minute
        if (minutesAgo <= 0) {
            "Just now"
        } else if (minutesAgo == 1) {
            "1 minute ago"
        } else "$minutesAgo minutes ago"
    }
        else if (hoursAgo == 1) {
            "1 hour ago"
        } else "$hoursAgo hours ago"
}

fun getFormattedTimeWithTodayTimeOrYesterdayTextOrSimpleDate(dateTime: LocalDateTime): String {
    val now = LocalDateTime.now()
    val today = now

    return when (dateTime.date) {
        today.date -> dateTime.formatAsTime()
        today.date.minusDays(1) -> "Yesterday"
        else -> dateTime.date.format("dd-MM-yyyy")
    }
}