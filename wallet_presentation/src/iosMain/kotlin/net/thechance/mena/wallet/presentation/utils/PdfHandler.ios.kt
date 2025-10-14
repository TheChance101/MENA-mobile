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
import kotlinx.io.IOException
import kotlinx.io.files.FileNotFoundException
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
import platform.CoreGraphics.CGContextScaleCTM
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
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.dataWithBytes
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.writeToFile
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import platform.UIKit.UIScreen
import platform.posix.memcpy
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Single
class PdfHandlerImpl : PdfHandler {
    @OptIn(ExperimentalForeignApi::class)
    override suspend fun splitToPagesOfPngs(pdfData: ByteArray): List<ByteArray> {
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

                val scale = UIScreen.mainScreen.scale * IMAGE_SCALE
                val scaledWidth = (pageRect.useContents { size.width } * scale).toULong()
                val scaledHeight = (pageRect.useContents { size.height } * scale).toULong()

                val colorSpace = CGColorSpaceCreateDeviceRGB()
                val bitmapInfo = kCGBitmapByteOrder32Big or kCGImageAlphaPremultipliedLast.value
                val context = CGBitmapContextCreate(
                    null,
                    scaledWidth,
                    scaledHeight,
                    8uL,
                    0uL,
                    colorSpace,
                    bitmapInfo
                ) ?: continue

                CGContextScaleCTM(context, scale, scale)

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
    private fun NSData.toByteArray(): ByteArray {
        val length = length.toInt()
        val byteArray = ByteArray(length)
        if (length > 0) {
            memcpy(byteArray.refTo(0), bytes, length.convert())
        }
        return byteArray
    }

    override suspend fun sharePdf(pdfData: ByteArray, fileName: String) {
        val url = saveToCache(pdfData, fileName)
        val activityViewController = UIActivityViewController(listOf(url), null)
        UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
            activityViewController, animated = true, completion = null
        )
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun savePdf(byteArray: ByteArray, location: StorageLocation): String {
        return withContext(Dispatchers.IO) {
            when (location) {
                is StorageLocation.Cache -> saveToCache(byteArray, location.fileName)
                is StorageLocation.Downloads -> saveToDocuments(byteArray, location.fileName)
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun deletePdf(location: StorageLocation) {
        withContext(Dispatchers.IO) {
            val fileManager = NSFileManager.defaultManager
            val path = getFilePath(location)

            if (fileManager.fileExistsAtPath(path)) {
                fileManager.removeItemAtPath(path, error = null)
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun getPdfBytes(location: StorageLocation): ByteArray {
        return withContext(Dispatchers.IO) {
            val path = getFilePath(location)

            val nsData = NSData.dataWithContentsOfFile(path)
                ?: throw FileNotFoundException("File not found: $path")

            nsData.toByteArray()
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun checkIfPdfExists(location: StorageLocation): Boolean {
        return withContext(Dispatchers.IO) {
            val fileManager = NSFileManager.defaultManager
            val path = getFilePath(location)

            fileManager.fileExistsAtPath(path)
        }
    }

    private fun getFilePath(location: StorageLocation): String {
        return when (location) {
            is StorageLocation.Cache -> {
                val tempDir = NSTemporaryDirectory()
                "$tempDir${location.fileName}"
            }
            is StorageLocation.Downloads -> {
                val documentsPath = getDocumentsDirectory()
                "$documentsPath/$APP_DOWNLOADS_FOLDER/${location.fileName}"
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun saveToCache(bytes: ByteArray, name: String): String {
        val tempDir = NSTemporaryDirectory()
        val filePath = tempDir + name
        val saved = bytes.usePinned {
            val nsData = NSData.dataWithBytes(it.addressOf(0), bytes.size.toULong())
            nsData.writeToFile(filePath, true)
        }
        val url = if (saved) NSURL.fileURLWithPath(filePath) else null
        return url?.path ?: throw IOException("Failed to save to cache")
    }

    @OptIn(ExperimentalForeignApi::class, ExperimentalTime::class)
    private fun saveToDocuments(
        pdfData: ByteArray,
        fileName: String
    ): String {
        val uniqueFileName = "${fileName}_${Clock.System.now().epochSeconds}"

        val fileManager = NSFileManager.defaultManager
        val documentsPath = getDocumentsDirectory()
        val appFolderPath = "$documentsPath/$APP_DOWNLOADS_FOLDER"

        if (!fileManager.fileExistsAtPath(appFolderPath)) {
            val created = fileManager.createDirectoryAtPath(
                appFolderPath,
                withIntermediateDirectories = true,
                attributes = null,
                error = null
            )
            if (!created) {
                throw IOException("Failed to create directory")
            }
        }

        val filePath = "$appFolderPath/$uniqueFileName"

        val saved = pdfData.usePinned { pinned ->
            val nsData = NSData.dataWithBytes(
                pinned.addressOf(0),
                pdfData.size.toULong()
            )
            nsData.writeToFile(filePath, atomically = true)
        }

        if (!saved) {
            throw IOException("Failed to save file")
        }

        return "$APP_DOWNLOADS_FOLDER/$uniqueFileName"
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun getDocumentsDirectory(): String {
        val paths = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true
        )
        return paths.first() as String
    }

    private companion object {
        // Chosen as a good balance between rendering time and image sharpness
        const val IMAGE_SCALE = 1.67f
        const val APP_DOWNLOADS_FOLDER = "MENA"
    }
}

actual fun getPdfHandler(): PdfHandler {
    return PdfHandlerImpl()
}