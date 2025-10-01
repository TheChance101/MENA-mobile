package net.thechance.mena.wallet.presentation.utils

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.refTo
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import platform.CoreGraphics.CGContextDrawPDFPage
import platform.CoreGraphics.CGDataConsumerCreateWithCFData
import platform.CoreGraphics.CGDataConsumerRelease
import platform.CoreGraphics.CGDataProviderCreateWithCFData
import platform.CoreGraphics.CGDataProviderRelease
import platform.CoreGraphics.CGPDFContextBeginPage
import platform.CoreGraphics.CGPDFContextClose
import platform.CoreGraphics.CGPDFContextCreate
import platform.CoreGraphics.CGPDFContextEndPage
import platform.CoreGraphics.CGPDFDocumentCreateWithProvider
import platform.CoreGraphics.CGPDFDocumentGetNumberOfPages
import platform.CoreGraphics.CGPDFDocumentGetPage
import platform.CoreGraphics.CGPDFDocumentRelease
import platform.Foundation.NSData
import platform.Foundation.NSMutableData
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.create
import platform.Foundation.dataWithBytes
import platform.Foundation.writeToFile
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

@Single
actual class PdfHandler {
    @Suppress("CAST_NEVER_SUCCEEDS")
    actual suspend fun splitToPagesOfPngs(pdfData: ByteArray): List<ByteArray> {
        val pages = mutableListOf<ByteArray>()

        // Convert ByteArray to NSData
        val data = pdfData.toNSData()
        val provider = CGDataProviderCreateWithCFData(data)
        val document = CGPDFDocumentCreateWithProvider(provider) ?: return emptyList()

        try {
            val pageCount = CGPDFDocumentGetNumberOfPages(document)
            for (pageNumber in 1..pageCount) {
                // Create a new PDF context for each page
                val outputData = NSMutableData()
                val dataConsumer = CGDataConsumerCreateWithCFData(outputData)
                val pdfContext = CGPDFContextCreate(dataConsumer, null, null)

                // Get the page
                val page = CGPDFDocumentGetPage(document, pageNumber)
                if (page != null) {
                    // Begin a new page in the context
                    CGPDFContextBeginPage(pdfContext, null)
                    CGContextDrawPDFPage(pdfContext, page)
                    CGPDFContextEndPage(pdfContext)
                }

                // Close the context and get the data
                CGPDFContextClose(pdfContext)
                pages.add(outputData.toByteArray())

                // Clean up
                CGDataConsumerRelease(dataConsumer)
            }
        } finally {
            CGPDFDocumentRelease(document)
            CGDataProviderRelease(provider)
        }

        return pages
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    fun ByteArray.toNSData(): NSData = usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
    }

    @OptIn(ExperimentalForeignApi::class)
    fun NSData.toByteArray(): ByteArray {
        val length = length.toInt()
        val byteArray = ByteArray(length)
        if (length > 0) {
            memcpy(byteArray.refTo(0), bytes, length.convert())
        }
        return byteArray
    }

    actual suspend fun sharePdf(pdfData: ByteArray, fileName: String) {
        val url = withContext(Dispatchers.IO) {
            saveFile(pdfData, fileName)
        }
        val activityViewController = UIActivityViewController(listOf(url), null)
        UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
            activityViewController, animated = true, completion = null
        )
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun saveFile(bytes: ByteArray, name: String): NSURL? {
        val tempDir = NSTemporaryDirectory()
        val sharedFile = tempDir + name
        val saved = bytes.usePinned {
            val nsData = NSData.dataWithBytes(it.addressOf(0), bytes.size.toULong())
            nsData.writeToFile(sharedFile, true)
        }
        return if (saved) NSURL.fileURLWithPath(sharedFile) else null
    }
}