package net.thechance.mena.trends.data.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.RawSource
import kotlinx.io.asSource
import org.koin.java.KoinJavaComponent.getKoin
import java.io.ByteArrayOutputStream

actual fun getPlatformFileReader(): VideoFileHandler = VideoFileHandlerImpl(getKoin().get<Context>())

class VideoFileHandlerImpl(
    private val context: Context
): VideoFileHandler {

    override suspend fun readFile(filePath: String): RawSource {
        return withContext(Dispatchers.IO){
            context
                .contentResolver
                .openInputStream(filePath.toUri())
                ?.asSource()
                ?: throw Exception("Failed to open input stream")
        }
    }

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
                            val frameData = bitmapToByteArray(bitmap)
                            bitmap.recycle()
                            frameData
                        }
                    }
            }
        }.getOrNull()
    }

    private fun bitmapToByteArray(
        bitmap: Bitmap,
        format: Bitmap.CompressFormat= Bitmap.CompressFormat.JPEG,
        quality: Int = 100
    ): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(format, quality, stream)
        return stream.toByteArray()
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