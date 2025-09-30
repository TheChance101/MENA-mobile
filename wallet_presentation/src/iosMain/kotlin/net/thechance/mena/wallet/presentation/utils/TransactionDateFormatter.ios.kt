package net.thechance.mena.wallet.presentation.utils

import kotlinx.datetime.LocalDateTime
import platform.Foundation.NSDateFormatter
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import platform.Foundation.NSDate
import platform.Foundation.NSTimeZone
import platform.Foundation.localTimeZone
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
actual fun formatTransactionDate(date: LocalDateTime, outputFormat: String): String {
    val timeZone1 = TimeZone.currentSystemDefault()
    val instant = date.toInstant(timeZone1).epochSeconds

    val formatter = NSDateFormatter().apply {
        dateFormat = outputFormat
        timeZone = NSTimeZone.localTimeZone
    }
    val date = NSDate(instant.toDouble() / 1000)
    return formatter.stringFromDate(date)
}