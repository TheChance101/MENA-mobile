@file:OptIn(ExperimentalTime::class)

package net.thechance.mena.core_chat.presentation.utils

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

fun LocalDateTime.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime {
    return Clock.System.now().toLocalDateTime(timeZone)
}


fun LocalDateTime.minusMinutes(
    minutes: Int,
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): LocalDateTime {
    return this.toInstant(timeZone)
        .minus(minutes.minutes)
        .toLocalDateTime(timeZone)
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
        .replace("MM", month.ordinal.toString().padStart(2, '0'))
        .replace("yyyy", year.toString())
}