package net.thechance.mena.admin_panel.data.mapper

import kotlinx.datetime.LocalDate

fun parseLocalDateOrDefault(value: String?) = value?.let {
    runCatching { LocalDate.parse(it) }
        .getOrDefault(DEFAULT_DATE)
} ?: DEFAULT_DATE

private val DEFAULT_DATE = LocalDate(9999, 12, 31)