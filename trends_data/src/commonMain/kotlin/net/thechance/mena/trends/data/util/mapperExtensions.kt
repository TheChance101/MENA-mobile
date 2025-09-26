package net.thechance.mena.trends.data.util

import kotlinx.datetime.LocalDateTime

fun Int?.orZero(): Int = this ?: 0


fun String?.parseDateStringOrNull(): String? {
    if (this.isNullOrBlank()) return null
    return try {
        LocalDateTime.parse(this)
        this
    } catch (e: Exception) {
        null
    }
}