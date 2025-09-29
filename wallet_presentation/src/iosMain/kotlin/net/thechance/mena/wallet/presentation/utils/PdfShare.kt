package net.thechance.mena.wallet.presentation.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.dataWithBytes
import platform.Foundation.writeToFile
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

@OptIn(ExperimentalForeignApi::class)
class PdfShareImpl : PdfShare {
    override suspend fun sharePdf(
        pdfBytes: ByteArray,
        fileName: String,
        mimeType: String
    ) {
        val url = withContext(Dispatchers.IO) {
            savePdfFile(pdfBytes, fileName)
        }

        url?.let { pdfUrl ->
            val activityViewController = UIActivityViewController(
                activityItems = listOf(pdfUrl),
                applicationActivities = null
            )

            UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
                activityViewController,
                animated = true,
                completion = null
            )
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun savePdfFile(bytes: ByteArray, name: String): NSURL? {
        val tempDir = NSTemporaryDirectory()
        val pdfFile = tempDir + name
        val saved = bytes.usePinned {
            val nsData = NSData.dataWithBytes(it.addressOf(0), bytes.size.toULong())
            nsData.writeToFile(pdfFile, true)
        }
        return if (saved) NSURL.fileURLWithPath(pdfFile) else null
    }
}

actual fun getPdfShare(): PdfShare {
    return PdfShareImpl()
}