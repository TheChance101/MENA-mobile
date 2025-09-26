package net.thechance.mena.trends.presentation.shared.util

import kotlin.math.roundToInt

expect suspend fun getVideoDuration(videoBytes: ByteArray): Long?

fun formatBytes(bytes: Long): String {
    val kb = 1024.0
    val mb = kb * 1024

    return when {
        bytes >= mb -> "${(bytes / mb).roundToInt()} MB"
        bytes >= kb -> "${(bytes / kb).roundToInt()} KB"
        else -> "$bytes B"
    }
}