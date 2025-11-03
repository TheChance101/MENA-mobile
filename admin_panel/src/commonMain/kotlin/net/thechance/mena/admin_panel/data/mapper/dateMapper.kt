package net.thechance.mena.admin_panel.data.mapper

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

fun parseLocalDateOrDefault(value: String?, defaultDate: LocalDate = DEFAULT_DATE) = value?.let {
    runCatching { LocalDate.parse(it) }.getOrDefault(defaultDate)
} ?: defaultDate

fun parseLocalDateTimeOrDefault(value: String?, defaultDateTime: LocalDateTime = DEFAULT_DATE_TIME) = value?.let {
    runCatching { LocalDateTime.parse(it) }
        .getOrDefault(DEFAULT_DATE_TIME)
}

private val DEFAULT_DATE = LocalDate(9999, 12, 31)
private val DEFAULT_DATE_TIME = LocalDateTime(DEFAULT_DATE, LocalTime(0, 0))