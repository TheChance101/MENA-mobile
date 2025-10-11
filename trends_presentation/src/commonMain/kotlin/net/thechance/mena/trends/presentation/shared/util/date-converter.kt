package net.thechance.mena.trends.presentation.shared.util

import kotlinx.datetime.LocalDateTime


fun LocalDateTime?.toFormattedString(): String? {
    return this?.let { date ->
        val day = date.day.toString().padStart(2, '0')
        val month = date.month.toString().padStart(2, '0')
        val year = date.year.toString()
        "$day-$month-$year"
    }
}