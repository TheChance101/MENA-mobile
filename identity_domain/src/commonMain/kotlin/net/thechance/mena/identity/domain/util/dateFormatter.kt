package net.thechance.mena.identity.domain.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd"

@OptIn(ExperimentalTime::class)
fun getCurrentDate(): LocalDate {
    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    return LocalDate(currentDate.year, currentDate.month, currentDate.day)
}

fun LocalDate?.orCurrentDate() = this ?: getCurrentDate()