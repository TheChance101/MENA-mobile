package net.thechance.mena.trends.presentation.shared.util.video_util

import org.koin.core.annotation.Single

@Single(binds = [VideoDurationExtractor::class])
class VideoDurationExtractorImpl: VideoDurationExtractor {
    override suspend fun getDuration(videoBytes: ByteArray): Long? {
        return getVideoDuration(videoBytes)
    }
}