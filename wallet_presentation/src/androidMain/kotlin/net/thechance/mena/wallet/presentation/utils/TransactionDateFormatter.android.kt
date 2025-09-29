package net.thechance.mena.wallet.presentation.utils

import android.os.Build
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDateTime
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter.ofPattern
import java.util.Date
import java.util.Locale
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
actual fun formatTransactionDate(date: LocalDateTime, outputFormat: String): String {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val formatter = ofPattern("dd MMM yyyy, h:mm a", Locale.getDefault())
        return date.toJavaLocalDateTime().format(formatter)
    } else {
        val timeZone1 = TimeZone.currentSystemDefault()
        val timeStamp = date.toInstant(timeZone1).epochSeconds
        val sdf = SimpleDateFormat(outputFormat, Locale.getDefault())
        return sdf.format(Date(timeStamp * 1000))
    }
}