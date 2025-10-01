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
class ImageSharerImpl : ImageSharer {
    override suspend fun shareImage(
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

actual fun getImageSharer(): ImageSharer {
    return ImageSharerImpl()
}