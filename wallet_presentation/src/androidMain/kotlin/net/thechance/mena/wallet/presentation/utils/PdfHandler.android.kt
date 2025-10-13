package net.thechance.mena.wallet.presentation.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.context.GlobalContext
import java.io.ByteArrayOutputStream
import java.io.File

class PdfHandlerImpl() : PdfHandler {
    private val context: Context = GlobalContext.get().get()
    private val fileManager: StatementFileManager by lazy {
        StatementFileManager(context)
    }

    override suspend fun splitToPagesOfPngs(pdfData: ByteArray): List<ByteArray> {
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

    override suspend fun sharePdf(pdfData: ByteArray, fileName: String) {
        val path = fileManager.saveFile(
            pdfData,
            StorageLocation.Cache(fileName)
        )

        val contentUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            File(path)
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooserIntent = Intent.createChooser(shareIntent, "Share PDF file")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(chooserIntent)
    }

    override suspend fun downloadPdf(pdfData: ByteArray, fileName: String): String {
        return fileManager.saveFile(
            pdfData,
            StorageLocation.Downloads(fileName)
        )
    }

    override suspend fun saveToCache(pdfData: ByteArray, fileName: String): String {
        return fileManager.saveFile(
            pdfData,
            StorageLocation.Cache(fileName)
        )
    }

    override suspend fun deleteStatement(location: StorageLocation) {
        fileManager.deleteFile(location)
    }

    override suspend fun getPdfBytes(location: StorageLocation): ByteArray {
        return fileManager.readFile(location)
    }

    override suspend fun checkIfStatementExists(location: StorageLocation): Boolean {
        return fileManager.checkIfFileExists(location)
    }

    companion object {
        private const val IMAGE_SCALE = 2f
    }
}

actual fun getPdfHandler(): PdfHandler {
    return PdfHandlerImpl()
}