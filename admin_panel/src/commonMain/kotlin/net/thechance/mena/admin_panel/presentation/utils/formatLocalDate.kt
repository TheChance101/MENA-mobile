package net.thechance.mena.admin_panel.presentation.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number
import java.time.format.DateTimeFormatter

fun LocalDateTime.format(outputPattern: String): String {
    val javaLocalDate = java.time.LocalDateTime.of(
        year,
        month.number,
        day,
        hour,
        minute,
        second,
        nanosecond
    )
    val formatter = DateTimeFormatter.ofPattern(outputPattern)
    return javaLocalDate.format(formatter)
}