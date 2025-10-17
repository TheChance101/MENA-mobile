package net.thechance.mena.identity.data.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.char

fun LocalDate.formatAsString(): String {
    val formatter = LocalDate.Format {
        year()
        char('-')
        monthNumber()
        char('-')
        day()
    }

    return this.format(formatter)
}