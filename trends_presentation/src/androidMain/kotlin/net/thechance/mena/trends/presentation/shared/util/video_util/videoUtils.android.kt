package net.thechance.mena.trends.presentation.shared.util.video_util

import android.media.MediaMetadataRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

actual suspend fun getVideoDuration(videoBytes: ByteArray): Long? {
    return withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        var tempFile: File? = null

        try {
            tempFile = File.createTempFile("temp_video", ".mp4")
            FileOutputStream(tempFile).use { it.write(videoBytes) }

            retriever.setDataSource(tempFile.absolutePath)
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)

            durationStr?.toLongOrNull()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            try {
                retriever.release()
                tempFile?.delete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}