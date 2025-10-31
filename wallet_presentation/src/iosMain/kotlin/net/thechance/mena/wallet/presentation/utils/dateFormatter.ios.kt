package net.thechance.mena.wallet.presentation.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSTimeZone
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.localTimeZone
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
actual fun formatLocalDateTime(date: LocalDateTime, outputFormat: String): String {
    val formatter = NSDateFormatter().apply {
        dateFormat = outputFormat
        timeZone = NSTimeZone.localTimeZone
    }
    val epochSeconds = date.toEpochSeconds()
    val nsDate = NSDate.dateWithTimeIntervalSince1970(epochSeconds)
    return formatter.stringFromDate(nsDate)
}

@OptIn(ExperimentalTime::class)
actual fun formatLocalDate(date: LocalDate, outputFormat: String): String {
    return formatLocalDateTime(date.atTime(0, 0), outputFormat)
}

@OptIn(ExperimentalTime::class)
private fun LocalDateTime.toEpochSeconds() = this.toInstant(TimeZone.currentSystemDefault()).epochSeconds.toDouble()