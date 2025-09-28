package net.thechance.mena.core_chat.data.chat.utils

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun LocalDateTime.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime {
    return Clock.System.now().toLocalDateTime(timeZone)
}


@OptIn(ExperimentalTime::class)
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