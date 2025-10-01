package net.thechance.mena.trends.presentation.shared.util.video_util

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