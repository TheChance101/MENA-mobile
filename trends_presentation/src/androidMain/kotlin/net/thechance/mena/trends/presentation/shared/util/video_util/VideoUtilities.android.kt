package net.thechance.mena.trends.presentation.shared.util.video_util

import android.media.MediaMetadataRetriever
import androidx.compose.ui.graphics.asImageBitmap
import io.github.vinceglb.filekit.dialogs.compose.util.encodeToByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual fun getVideoUtilities(): VideoUtilities {
    return VideoUtilitiesImpl()
}

class VideoUtilitiesImpl: VideoUtilities {

    override suspend fun getDuration(
        videoBytes: ByteArray
    ): Long? = withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        return@withContext runCatching {
            retriever.use {
                it.setDataSource(ByteArrayMediaSource(videoBytes))
                val durationStr =
                    it.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                durationStr?.toLongOrNull()
            }
        }.getOrNull()
    }


    override suspend fun extractVideoFrame(
        videoData: ByteArray, timeMs: Long
    ): ByteArray? = withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        return@withContext runCatching {
            retriever.use {
                it.setDataSource(ByteArrayMediaSource(videoData))
                it.getFrameAtTime(timeMs * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                    ?.let { bmp ->
                        val frameData = bmp.asImageBitmap().encodeToByteArray()
                        bmp.recycle()
                        frameData
                    }
            }
        }.getOrNull()
    }


    override suspend fun extractVideoFrame(
        videoData: ByteArray, percent: Float
    ): ByteArray? = withContext(Dispatchers.IO) {
        val duration = getDuration(videoData) ?: 1L
        val clamped = percent.coerceIn(0f, 1f)
        val targetTimeUs = (duration * clamped * 1000).toLong()
        return@withContext runCatching {
            extractVideoFrame(
                videoData,
                targetTimeUs
            )
        }.getOrNull()
    }

}

private inline fun <R> MediaMetadataRetriever.use(block: (MediaMetadataRetriever) -> R): R {
    try {
        return block(this)
    } finally {
        release()
    }
}