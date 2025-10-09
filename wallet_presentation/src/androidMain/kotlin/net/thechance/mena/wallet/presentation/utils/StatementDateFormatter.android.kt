package net.thechance.mena.wallet.presentation.utils

import android.os.Build
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toJavaLocalDate
import java.util.Date
import java.util.Locale
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
actual fun formatLocalDate(date: LocalDate, outputFormat: String): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val formatter =
            java.time.format.DateTimeFormatter.ofPattern(outputFormat, Locale.getDefault())
        date.toJavaLocalDate().format(formatter)
    } else {
        val timeZone = TimeZone.currentSystemDefault()
        val epochSeconds = date.atStartOfDayIn(timeZone).epochSeconds
        val sdf = java.text.SimpleDateFormat(outputFormat, Locale.getDefault())
        sdf.format(Date(epochSeconds * 1000))
    }
}