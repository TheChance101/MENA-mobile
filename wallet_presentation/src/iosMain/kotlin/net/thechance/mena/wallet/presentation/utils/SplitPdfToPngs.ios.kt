package net.thechance.mena.wallet.presentation.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.refTo
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
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
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import platform.UIKit.UIScreen
import platform.posix.memcpy

// Chosen as a good balance between rendering time and image sharpness
private const val IMAGE_SCALE = 1.67f

@OptIn(ExperimentalForeignApi::class)
actual suspend fun splitPdfToPngs(pdfData: ByteArray): List<ByteArray> {
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