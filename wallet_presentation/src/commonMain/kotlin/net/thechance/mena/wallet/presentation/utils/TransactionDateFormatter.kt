package net.thechance.mena.wallet.presentation.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number


fun formateTransactionDate(date: LocalDateTime): String {
    val monthNames = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )
    val amOrPm = if (date.hour < 12) "AM" else "PM"
    val displayHour = if (date.hour % 12 == 0) 12 else date.hour % 12

    return "${date.day} ${monthNames[date.month.number - 1]} ${date.year}, $displayHour:${
        date.minute.toString().padStart(2, '0')
    } $amOrPm"
}