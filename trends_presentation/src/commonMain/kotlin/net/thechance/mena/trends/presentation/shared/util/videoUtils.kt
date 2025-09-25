package net.thechance.mena.trends.presentation.shared.util

import kotlin.math.roundToInt

expect suspend fun getVideoDuration(videoBytes: ByteArray): Long?

fun formatDuration(durationMillis: Long): String {
    val totalSeconds = durationMillis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    val minutesStr = if (minutes < 10) "0$minutes" else "$minutes"
    val secondsStr = if (seconds < 10) "0$seconds" else "$seconds"

    return "$minutesStr:$secondsStr"
}

fun formatBytes(bytes: Long): String {
    val kb = 1024.0
    val mb = kb * 1024

    return when {
        bytes >= mb -> "${(bytes / mb).roundToInt()} MB"
        bytes >= kb -> "${(bytes / kb).roundToInt()} KB"
        else -> "$bytes B"
    }
}