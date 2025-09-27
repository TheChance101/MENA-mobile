package net.thechance.mena.trends.presentation.shared.util.video_util

import kotlin.math.roundToInt

expect suspend fun getVideoDuration(videoBytes: ByteArray): Long?

fun formatBytes(bytes: Long): String {
    val kb = 1024.0
    val mb = kb * 1024

    return when {
        bytes >= mb -> "${((bytes / mb) * 10).roundToInt() / 10.0} MB"
        bytes >= kb -> "${((bytes / kb) * 10).roundToInt() / 10.0} KB"
        else -> "$bytes B"
    }
}