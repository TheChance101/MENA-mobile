package net.thechance.mena.wallet.presentation.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.context.GlobalContext
import java.io.ByteArrayOutputStream
import java.io.File

// Chosen as a good balance between rendering time and image sharpness
private const val IMAGE_SCALE = 1.67f

actual suspend fun splitPdfToPngs(pdfData: ByteArray): List<ByteArray> {
    return withContext(Dispatchers.IO) {
        try {
            val fileManager = GlobalContext.get().get<FileManager>()

            val tempPath = fileManager.saveFile(pdfData, StorageLocation.Cache("temp.pdf"), "application/pdf")
            val tempFile = File(tempPath)

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
                tempFile.delete()
            }
        } catch (_: Exception) {
            return@withContext emptyList()
        }
    }
}