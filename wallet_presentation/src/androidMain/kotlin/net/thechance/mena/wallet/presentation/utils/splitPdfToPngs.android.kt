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

actual suspend fun splitPdfToPngs(pdfData: ByteArray): List<ByteArray> = withContext(Dispatchers.IO) {
    runCatching {
        val fileManager = GlobalContext.get().get<FileManager>()
        val tempFile = createTempPdfFile(fileManager, pdfData)
        renderPdfToPngs(tempFile)
    }.getOrElse { emptyList() }
}

private suspend fun createTempPdfFile(fileManager: FileManager, pdfData: ByteArray): File {
    val tempPath = fileManager.saveFile(
        data = pdfData,
        location = StorageLocation.Cache("temp.pdf"),
        mimeType = "application/pdf"
    )

    return File(tempPath).apply {
        if (!exists() || length() == 0L) {
            throw IllegalStateException("Temporary PDF file is empty or missing.")
        }
    }
}

private fun renderPdfToPngs(pdfFile: File): List<ByteArray> {
    val fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
    val renderer = PdfRenderer(fileDescriptor)

    return buildList {
        repeat(renderer.pageCount) { index ->
            val pageBytes = renderer.renderPageToPng(index)
            add(pageBytes)
        }
    }.also {
        renderer.close()
        fileDescriptor.close()
        pdfFile.delete()
    }
}

private fun PdfRenderer.renderPageToPng(pageIndex: Int): ByteArray {
    openPage(pageIndex).use { page ->
        val width = (page.width * IMAGE_SCALE).toInt()
        val height = (page.height * IMAGE_SCALE).toInt()
        val bitmap = createBitmap(width, height)

        val matrix = Matrix().apply {
            postScale(IMAGE_SCALE, IMAGE_SCALE)
        }

        page.render(bitmap, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        return ByteArrayOutputStream().use { output ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
            bitmap.recycle()
            output.toByteArray()
        }
    }
}