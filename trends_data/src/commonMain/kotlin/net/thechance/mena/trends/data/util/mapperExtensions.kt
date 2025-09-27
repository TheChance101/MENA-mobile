package net.thechance.mena.trends.data.util

import kotlinx.datetime.LocalDateTime

fun Int?.orZero(): Int = this ?: 0


fun String?.parseDateStringOrNull(): LocalDateTime? {
    if (this.isNullOrBlank()) return null
    return runCatching {
        LocalDateTime.parse(this)
    }.getOrNull()
}