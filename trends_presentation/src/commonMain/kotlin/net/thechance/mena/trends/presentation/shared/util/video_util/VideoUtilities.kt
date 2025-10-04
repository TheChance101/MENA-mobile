package net.thechance.mena.trends.presentation.shared.util.video_util

import kotlin.math.roundToInt

expect fun getVideoUtilities(): VideoUtilities

interface VideoUtilities {
    suspend fun getDuration(videoBytes: ByteArray): Long?

    suspend fun extractVideoFrame(
        videoData: ByteArray,
        timeMs: Long = 0L
    ): ByteArray?

    suspend fun extractVideoFrame(
        videoData: ByteArray,
        percent: Float
    ): ByteArray?
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

internal fun getMimeTypeFromExtension(extension: String): String?{
    return mimeTypes[extension]
}

private val mimeTypes = mapOf(
    "mp4" to "video/mp4",
    "mov" to "video/quicktime",
    "mkv" to "video/x-matroska"
)