package net.thechance.mena.wallet.data.mapper

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

fun parseLocalDateOrDefault(value: String?) = value?.let {
    runCatching { LocalDate.parse(it) }
        .getOrDefault(DEFAULT_DATE)
} ?: DEFAULT_DATE

fun parseLocalDateTimeOrDefault(value: String?) = value?.let {
    runCatching { LocalDateTime.parse(it) }
        .getOrDefault(DEFAULT_DATE_TIME)
} ?: DEFAULT_DATE_TIME

private val DEFAULT_DATE = LocalDate(9999, 12, 31)
private val DEFAULT_DATE_TIME = LocalDateTime(DEFAULT_DATE, LocalTime(0, 0))