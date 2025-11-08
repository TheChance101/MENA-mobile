package net.thechance.mena.trends.data.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

fun Int?.orZero(): Int = this ?: 0

fun Boolean?.orFalse(): Boolean = this ?: false

@OptIn(ExperimentalTime::class)
fun String?.parseDateStringOrNull(
    sourceTimeZone: TimeZone = TimeZone.UTC
): LocalDateTime? {
    if (this.isNullOrBlank()) return null
    return runCatching {
        LocalDateTime.parse(this)
            .toInstant(sourceTimeZone)
            .toLocalDateTime(TimeZone.currentSystemDefault())
    }.getOrNull()
}
