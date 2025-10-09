package net.thechance.mena.wallet.presentation.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSTimeZone
import platform.Foundation.localTimeZone
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
actual fun formatLocalDateTime(date: LocalDateTime, outputFormat: String): String {
    val timeZone1 = TimeZone.currentSystemDefault()
    val instant = date.toInstant(timeZone1).epochSeconds

    val formatter = NSDateFormatter().apply {
        dateFormat = outputFormat
        timeZone = NSTimeZone.localTimeZone
    }
    val date = NSDate(instant.toDouble() / 1000)
    return formatter.stringFromDate(date)
}