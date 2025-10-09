package net.thechance.mena.wallet.presentation.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSTimeZone
import platform.Foundation.localTimeZone
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
actual fun formatLocalDate(date: LocalDate, outputFormat: String): String {
    val timeZone = TimeZone.currentSystemDefault()
    val instant = date.atStartOfDayIn(timeZone).epochSeconds

    val formatter = NSDateFormatter().apply {
        dateFormat = outputFormat
        this.timeZone = NSTimeZone.localTimeZone
    }
    val nsDate = NSDate(instant.toDouble() * 1000)
    return formatter.stringFromDate(nsDate)
}
