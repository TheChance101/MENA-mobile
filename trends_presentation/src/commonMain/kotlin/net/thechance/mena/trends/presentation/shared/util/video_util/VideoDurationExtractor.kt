package net.thechance.mena.trends.presentation.shared.util.video_util

interface VideoDurationExtractor {
    suspend fun getDuration(videoBytes: ByteArray): Long?
}