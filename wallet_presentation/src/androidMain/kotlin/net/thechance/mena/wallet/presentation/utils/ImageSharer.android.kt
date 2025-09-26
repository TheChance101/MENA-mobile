package net.thechance.mena.wallet.presentation.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Provided
import org.koin.core.context.GlobalContext
import java.io.File

actual class ImageSharer ( @Provided private val context: Context) {
    actual suspend fun shareImage(
        imageBytes: ByteArray,
        fileName: String,
        mimeType: String
    ) {
        val contentUri = withContext(Dispatchers.IO) {
            val file = File(context.cacheDir, fileName)
            file.writeBytes(imageBytes)
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooserIntent = Intent.createChooser(shareIntent, "Share Image")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(chooserIntent)
    }

    actual suspend fun saveImageToGallery(
        imageBytes: ByteArray,
        fileName: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    saveImageToGalleryNewVersions(imageBytes, fileName)
                } else {
                    saveImageToGalleryLegacy(imageBytes, fileName)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveImageToGalleryNewVersions(imageBytes: ByteArray, fileName: String): Boolean {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        return uri?.let {
            try {
                resolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(imageBytes)
                }
                true
            } catch (e: Exception) {
                false
            }
        } ?: false
    }

    private fun saveImageToGalleryLegacy(imageBytes: ByteArray, fileName: String): Boolean {
        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        if (!picturesDir.exists()) {
            picturesDir.mkdirs()
        }

        val file = File(picturesDir, fileName)

        return try {
            file.writeBytes(imageBytes)

            MediaScannerConnection.scanFile(
                context,
                arrayOf(file.absolutePath),
                arrayOf("image/png"),
                null
            )
            true
        } catch (e: Exception) {
            false
        }
    }
}

actual fun getImageSharer(): ImageSharer {
    val context = GlobalContext.get().get<Context>()
    return ImageSharer(context)
}