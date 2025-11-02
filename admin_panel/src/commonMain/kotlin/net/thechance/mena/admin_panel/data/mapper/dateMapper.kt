package net.thechance.mena.admin_panel.data.mapper

import kotlinx.datetime.LocalDate

fun parseLocalDateOrDefault(value: String?, defaultDate: LocalDate = DEFAULT_DATE) = value?.let {
    runCatching { LocalDate.parse(it) }.getOrDefault(defaultDate)
} ?: defaultDate
private val DEFAULT_DATE = LocalDate(9999, 12, 31)