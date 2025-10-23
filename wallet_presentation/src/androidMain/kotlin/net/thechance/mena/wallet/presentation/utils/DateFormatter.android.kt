package net.thechance.mena.wallet.presentation.utils

import android.os.Build
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDateTime
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter.ofPattern
import java.util.Date
import java.util.Locale
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
actual fun formatLocalDateTime(date: LocalDateTime, outputFormat: String): String {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val formatter = ofPattern(outputFormat, Locale.getDefault())
        return date.toJavaLocalDateTime().format(formatter)
    } else {
        val sdf = SimpleDateFormat(outputFormat, Locale.getDefault())
        return sdf.format(Date(date.toEpochMillis()))
    }
}

@OptIn(ExperimentalTime::class)
actual fun formatLocalDate(date: LocalDate, outputFormat: String): String {
    return formatLocalDateTime(date.atTime(0, 0), outputFormat)
}

@OptIn(ExperimentalTime::class)
private fun LocalDateTime.toEpochMillis() = this.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()