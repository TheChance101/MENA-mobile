package net.thechance.mena.identity.data.dataSource.local.storage

import android.content.Context
import android.provider.MediaStore
import androidx.core.content.contentValuesOf

actual class ImagesGalleryManager(private val context: Context) {
    actual suspend fun saveImage(imageBytes: ByteArray) {
        val contentValues = contentValuesOf(
            MediaStore.Images.Media.MIME_TYPE to "image/png",
            MediaStore.Images.Media.RELATIVE_PATH to "DCIM/Mena"
        )
        val resolver = context.contentResolver
        val uri = resolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        uri?.let {
            resolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(imageBytes)
            }
        }
    }
}