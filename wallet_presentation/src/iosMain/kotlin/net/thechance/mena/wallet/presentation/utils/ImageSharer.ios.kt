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
import platform.UIKit.UIImage
import platform.UIKit.UIImageWriteToSavedPhotosAlbum

@OptIn(ExperimentalForeignApi::class)
actual class ImageSharer {

    actual suspend fun shareImage(
        imageBytes: ByteArray,
        fileName: String,
        mimeType: String
    ) {
        val url = withContext(Dispatchers.IO) {
            saveFile(imageBytes, fileName)
        }
        val activityViewController = UIActivityViewController(listOf(url), null)
        UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
            activityViewController, animated = true, completion = null
        )
    }

    actual suspend fun saveImageToGallery(
        imageBytes: ByteArray,
        fileName: String
    ): Boolean {
        return withContext(Dispatchers.Main) {
            try {
                val uiImage = byteArrayToUIImage(imageBytes) ?: return@withContext false
                UIImageWriteToSavedPhotosAlbum(uiImage, null, null, null)
                true
            } catch (e: Exception) {
                throw Exception("Error saving image to gallery: ${e.message}")
            }
        }
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

    private fun byteArrayToUIImage(imageBytes: ByteArray): UIImage? {
        return imageBytes.usePinned { pinned ->
            val nsData = NSData.dataWithBytes(
                bytes = pinned.addressOf(0),
                length = imageBytes.size.toULong()
            )
            return UIImage.imageWithData(nsData)
        }
    }
}

actual fun getImageSharer(): ImageSharer {
    return ImageSharer()
}