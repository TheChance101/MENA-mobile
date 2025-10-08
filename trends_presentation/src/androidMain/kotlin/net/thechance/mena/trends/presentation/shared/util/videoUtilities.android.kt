package net.thechance.mena.trends.presentation.shared.util

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.net.toUri
import io.github.vinceglb.filekit.dialogs.compose.util.encodeToByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.mp.KoinPlatform.getKoin

actual fun getVideoUtilities(): VideoUtilities {
    return VideoUtilitiesImpl(getKoin().get<Context>())
}

class VideoUtilitiesImpl(
    private val context: Context
): VideoUtilities {

    override suspend fun getDuration(filePath: String): Long? {
        return withContext(Dispatchers.IO) {
            runCatching {
                MediaMetadataRetriever()
                    .use(filePath.toUri(), context) {
                        this.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                            ?.toLongOrNull()
                    }
            }.getOrNull()
        }
    }

    override suspend fun extractVideoFrame(filePath: String, timeMs: Long): ByteArray? {
        return withContext(Dispatchers.IO) {
            runCatching {
                MediaMetadataRetriever()
                    .use(filePath.toUri(), context) {
                        this.getFrameAtTime(
                            timeMs * MILLISECOND_SECOND_CONVERSION,
                            MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                        )?.let { bitmap ->
                            val frameData = bitmap.asImageBitmap().encodeToByteArray()
                            bitmap.recycle()
                            frameData
                        }
                    }
            }
        }.getOrNull()
    }

    override suspend fun extractVideoFrame(filePath: String, percent: Float): ByteArray? {
        return withContext(Dispatchers.IO) {
            val duration = getDuration(filePath) ?: 1L
            val clamped = percent.coerceIn(0f, 1f)
            val targetTimeUs = (duration * clamped * MILLISECOND_SECOND_CONVERSION).toLong()
            runCatching {
                extractVideoFrame(filePath, targetTimeUs)
            }.getOrNull()
        }
    }

    private inline fun <R> MediaMetadataRetriever.use(
        fileUri: Uri,
        context: Context,
        block: MediaMetadataRetriever.() -> R
    ): R {
        try {
            return this.apply {
                setDataSource(context, fileUri)
            }.block()
        } finally {
            release()
        }
    }

    private companion object{
        private const val MILLISECOND_SECOND_CONVERSION = 1000
    }
}