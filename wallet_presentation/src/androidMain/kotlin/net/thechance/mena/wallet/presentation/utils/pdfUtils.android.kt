package net.thechance.mena.wallet.presentation.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import org.koin.core.context.GlobalContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

@Single
actual class PdfHandler{

    private val context = GlobalContext.get().get<Context>()

    actual suspend fun splitToPagesOfPngs(pdfData: ByteArray): List<ByteArray> {
        return withContext(Dispatchers.IO) {
            try {

                val tempFile = File(context.cacheDir, "statement.pdf")
                    .apply { writeBytes(pdfData) }

                if (!tempFile.exists() || tempFile.length() == 0L)
                    throw RuntimeException("empty file")

                val fileDescriptor =
                    ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)

                val renderer = PdfRenderer(fileDescriptor)

                buildList {
                    repeat(renderer.pageCount) { pageNum ->
                        val page = renderer.openPage(pageNum)

                        val width = (page.width * IMAGE_SCALE).toInt()
                        val height = (page.height * IMAGE_SCALE).toInt()

                        val bitmap = createBitmap(width, height)

                        val matrix = Matrix().apply {
                            postScale(IMAGE_SCALE, IMAGE_SCALE)
                        }

                        page.render(
                            bitmap,
                            null,
                            matrix,
                            PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                        )

                        val output = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                        add(output.toByteArray())

                        bitmap.recycle()
                        page.close()
                    }
                    renderer.close()
                    fileDescriptor.close()
                }
            } catch (_: Exception) {
                return@withContext emptyList()
            }
        }
    }

    actual suspend fun sharePdf(pdfData: ByteArray, fileName: String) {
        val contentUri = withContext(Dispatchers.IO) {
            val file = File(context.cacheDir, fileName)
            file.writeBytes(pdfData)
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = MIME_TYPE
            putExtra(Intent.EXTRA_STREAM, contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooserIntent = Intent.createChooser(shareIntent, "Share PDF file")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(chooserIntent)
    }

    actual suspend fun downloadPdf(pdfData: ByteArray, fileName: String): String {
        return withContext(Dispatchers.IO) {
            val specialFileName = generateSpecialFileName(fileName)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveToMediaStore(specialFileName, pdfData)
            } else {
                saveToLegacyStorage(specialFileName, pdfData)
            }
        }
    }

    private fun generateSpecialFileName(baseName: String): String {
        val timestamp = System.currentTimeMillis()
        return "${baseName}_$timestamp"
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveToMediaStore(fileName: String, bytes: ByteArray): String {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.pdf")
            put(MediaStore.MediaColumns.MIME_TYPE, MIME_TYPE)
            put(MediaStore.MediaColumns.RELATIVE_PATH, "$DOWNLOAD_DIR_BASE/$APP_DOWNLOADS_FOLDER")
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            ?: throw IOException()

        resolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(bytes)
        } ?: throw IOException()

        val localizedDownloads = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS
        ).name

        return "$localizedDownloads/$APP_DOWNLOADS_FOLDER/$fileName.pdf"
    }

    @Suppress("DEPRECATION")
    private fun saveToLegacyStorage(fileName: String, bytes: ByteArray): String {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS
        )
        val menaFolder = File(downloadsDir, APP_DOWNLOADS_FOLDER)

        if (!menaFolder.exists() && !menaFolder.mkdirs()) {
            throw IOException()
        }

        val file = File(menaFolder, "$fileName.pdf")
        file.writeBytes(bytes)

        return "${downloadsDir.name}/$APP_DOWNLOADS_FOLDER/$fileName.pdf"
    }

    private companion object {
        const val IMAGE_SCALE = 1.67f
        const val DOWNLOAD_DIR_BASE = "Download"
        const val APP_DOWNLOADS_FOLDER = "MENA"
        const val MIME_TYPE = "application/pdf"
    }
}