package net.thechance.mena.trends.data.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import androidx.core.net.toUri
import kotlinx.io.RawSource
import kotlinx.io.asSource
import net.thechance.mena.trends.domain.exception.FileNotFoundException
import org.koin.java.KoinJavaComponent.getKoin
import java.io.ByteArrayOutputStream

actual fun getPlatformFileReader(): VideoFileHandler = VideoFileHandlerImpl(getKoin().get<Context>())

class VideoFileHandlerImpl(
    private val context: Context
): VideoFileHandler {
    override fun readFile(filePath: String): RawSource {
        return context.contentResolver
            .openInputStream(filePath.toUri())
            ?.asSource()
            ?: throw FileNotFoundException()
    }

    override suspend fun getDuration(filePath: String): Long? {
        return useMetadataRetriever(filePath) {
            extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()
        }
    }

    override suspend fun getMimeType(filePath: String): String {
        return useMetadataRetriever(filePath) {
            extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
        } ?: DEFAULT_MIME_TYPE
    }

    override suspend fun extractVideoFrame(filePath: String, timeMs: Long): ByteArray? {
        return useMetadataRetriever(filePath) {
            getFrameAtTime(
                timeMs * MILLE_SECOND_CONVERSION,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC
            )?.let { bitmap ->
                bitmapToByteArray(bitmap).also { bitmap.recycle() }
            }
        }
    }

    private fun bitmapToByteArray(
        bitmap: Bitmap,
        format: Bitmap.CompressFormat= Bitmap.CompressFormat.JPEG,
        quality: Int = COMPRESS_QUALITY
    ): ByteArray {
        return ByteArrayOutputStream().use { stream ->
            bitmap.compress(format, quality, stream)
            stream.toByteArray()
        }
    }

    private inline fun <R> useMetadataRetriever(
        filePath: String,
        block: MediaMetadataRetriever.() -> R
    ): R? {
        return runCatching {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(context, filePath.toUri())
                retriever.block()
            } finally {
                retriever.release()
            }
        }.getOrNull()
    }

    private companion object{
        const val MILLE_SECOND_CONVERSION = 1000
        const val COMPRESS_QUALITY = 100
        const val DEFAULT_MIME_TYPE = "application/octet-stream"
    }
}