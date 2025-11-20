package net.thechance.mena.trends.data.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun getLastMidnightTime(): LocalDateTime {
    val todayData = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date
    return LocalDateTime(todayData, LocalTime(0, 0))
}