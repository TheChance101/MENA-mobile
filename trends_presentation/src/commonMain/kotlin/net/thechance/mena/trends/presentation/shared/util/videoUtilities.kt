package net.thechance.mena.trends.presentation.shared.util

import kotlin.math.roundToInt

expect fun getVideoUtilities(): VideoUtilities

interface VideoUtilities {
    suspend fun getDuration(filePath: String): Long?
    suspend fun extractVideoFrame(filePath: String, timeMs: Long = 0L): ByteArray?
    suspend fun extractVideoFrame(filePath: String, percent: Float): ByteArray?
}

internal fun formatBytes(bytes: Long): String {
    val kb = 1024.0
    val mb = kb * 1024

    return when {
        bytes >= mb -> "${((bytes / mb) * 10).roundToInt() / 10.0} MB"
        bytes >= kb -> "${((bytes / kb) * 10).roundToInt() / 10.0} KB"
        else -> "$bytes B"
    }
}