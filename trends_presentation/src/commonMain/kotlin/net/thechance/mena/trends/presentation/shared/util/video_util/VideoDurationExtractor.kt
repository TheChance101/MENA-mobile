package net.thechance.mena.trends.presentation.shared.util.video_util

expect fun getVideoDurationExtractor(): VideoDurationExtractor

interface VideoDurationExtractor {
    suspend fun getDuration(videoBytes: ByteArray): Long?
}
