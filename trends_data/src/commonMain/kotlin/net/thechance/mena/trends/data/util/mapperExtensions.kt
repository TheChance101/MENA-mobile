package net.thechance.mena.trends.data.util

import kotlinx.datetime.LocalDateTime

fun Int?.orZero(): Int = this ?: 0

fun Boolean?.orFalse(): Boolean = this ?: false

fun String?.parseDateStringOrNull(): LocalDateTime? {
    if (this.isNullOrBlank()) return null
    return runCatching {
        LocalDateTime.parse(this)
    }.getOrNull()
}