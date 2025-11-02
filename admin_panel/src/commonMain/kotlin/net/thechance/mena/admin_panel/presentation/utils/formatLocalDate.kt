package net.thechance.mena.admin_panel.presentation.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import java.time.format.DateTimeFormatter

fun LocalDate.format(outputPattern: String): String {
    val javaLocalDate = java.time.LocalDate.of(year, month.number, day)
    val formatter = DateTimeFormatter.ofPattern(outputPattern)
    return javaLocalDate.format(formatter)
}