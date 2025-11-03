@file:OptIn(ExperimentalTime::class)

package net.thechance.mena.wallet.data.mapper

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

fun parseLocalDateOrDefault(value: String?): LocalDate {
    return value?.let {
        runCatching {
            val normalized = if(it.contains("T")) it else it + "T00:00:00Z"
            val instant = Instant.parse(normalized)
            val userZone = TimeZone.currentSystemDefault()
            instant.toLocalDateTime(userZone).date
        }.getOrDefault(DEFAULT_DATE)
    } ?: DEFAULT_DATE
}

fun parseLocalDateTimeOrDefault(value: String?): LocalDateTime {
    return value?.let {
        runCatching {
            val normalized = if (it.endsWith("Z") || it.contains('+')) it else it + "Z"
            val instant = Instant.parse(normalized)
            val userZone = TimeZone.currentSystemDefault()
            instant.toLocalDateTime(userZone)
        }.getOrDefault(DEFAULT_DATE_TIME)
    } ?: DEFAULT_DATE_TIME
}
private val DEFAULT_DATE = LocalDate(9999, 12, 31)
private val DEFAULT_DATE_TIME = LocalDateTime(DEFAULT_DATE, LocalTime(0, 0))