package net.thechance.mena.wallet.presentation.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.refTo
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import platform.CoreFoundation.CFDataCreateWithBytesNoCopy
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreFoundation.kCFAllocatorNull
import platform.CoreGraphics.CGBitmapContextCreate
import platform.CoreGraphics.CGBitmapContextCreateImage
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGColorSpaceRelease
import platform.CoreGraphics.CGContextDrawPDFPage
import platform.CoreGraphics.CGContextFillRect
import platform.CoreGraphics.CGContextRelease
import platform.CoreGraphics.CGContextSetRGBFillColor
import platform.CoreGraphics.CGDataProviderCreateWithCFData
import platform.CoreGraphics.CGDataProviderRelease
import platform.CoreGraphics.CGImageAlphaInfo.kCGImageAlphaPremultipliedLast
import platform.CoreGraphics.CGImageRelease
import platform.CoreGraphics.CGPDFDocumentCreateWithProvider
import platform.CoreGraphics.CGPDFDocumentGetNumberOfPages
import platform.CoreGraphics.CGPDFDocumentGetPage
import platform.CoreGraphics.CGPDFDocumentRelease
import platform.CoreGraphics.CGPDFPageGetBoxRect
import platform.CoreGraphics.kCGBitmapByteOrder32Big
import platform.CoreGraphics.kCGPDFMediaBox
import platform.Foundation.NSData
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.dataWithBytes
import platform.Foundation.writeToFile
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import platform.posix.memcpy

@Single
actual class PdfHandler {
    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun splitToPagesOfPngs(pdfData: ByteArray): List<ByteArray> {
        val pages = mutableListOf<ByteArray>()
        val cfData = pdfData.usePinned { pinned ->
            CFDataCreateWithBytesNoCopy(
                kCFAllocatorDefault,
                pinned.addressOf(0).reinterpret(),
                pdfData.size.toLong(),
                kCFAllocatorNull
            )
        }
        val provider = CGDataProviderCreateWithCFData(cfData)
        val document = CGPDFDocumentCreateWithProvider(provider) ?: return emptyList()
        try {
            val pageCount = CGPDFDocumentGetNumberOfPages(document)
            for (pageNumber in 1..pageCount.toInt()) {
                val page = CGPDFDocumentGetPage(document, pageNumber.toULong()) ?: continue
                val pageRect = CGPDFPageGetBoxRect(page, kCGPDFMediaBox)
                val width = pageRect.useContents { size.width }
                val height = pageRect.useContents { size.height }
                val colorSpace = CGColorSpaceCreateDeviceRGB()
                val bitmapInfo = kCGBitmapByteOrder32Big or kCGImageAlphaPremultipliedLast.value
                val context = CGBitmapContextCreate(
                    null,
                    width.toULong(),
                    height.toULong(),
                    8uL,
                    0uL,
                    colorSpace,
                    bitmapInfo
                ) ?: continue

                CGContextSetRGBFillColor(context, 1.0, 1.0, 1.0, 1.0)
                CGContextFillRect(context, pageRect)

                CGContextDrawPDFPage(context, page)

                val cgImage = CGBitmapContextCreateImage(context) ?: continue
                val uiImage = UIImage(cgImage)

                val pngData = UIImagePNGRepresentation(uiImage) ?: continue
                pages.add(pngData.toByteArray())

                CGImageRelease(cgImage)
                CGContextRelease(context)
                CGColorSpaceRelease(colorSpace)
            }
        } finally {
            CGPDFDocumentRelease(document)
            CGDataProviderRelease(provider)
        }
        return pages
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