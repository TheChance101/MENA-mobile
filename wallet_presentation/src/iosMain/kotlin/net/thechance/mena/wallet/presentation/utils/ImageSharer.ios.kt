package net.thechance.mena.wallet.presentation.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageWriteToSavedPhotosAlbum
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow

@OptIn(ExperimentalForeignApi::class)
actual class ImageSharer {

    actual fun shareImage(
        imageBytes: ByteArray,
        fileName: String,
        mimeType: String
    ) {
        MainScope().launch {
            try {
                val uiImage = byteArrayToUIImage(imageBytes) ?: return@launch
                shareImageIOS(uiImage)
            } catch (e: Exception) {
                throw Exception("Error sharing image: ${e.message}")
            }
        }
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

    private fun byteArrayToUIImage(imageBytes: ByteArray): UIImage? {
        return imageBytes.usePinned { pinned ->
            val nsData = NSData.dataWithBytes(
                bytes = pinned.addressOf(0),
                length = imageBytes.size.toULong()
            )
            return UIImage.imageWithData(nsData)
        }
    }

    private fun shareImageIOS(image: UIImage) {
        val activityViewController = UIActivityViewController(
            activityItems = listOf(image),
            applicationActivities = null
        )

        val rootViewController = getRootViewController()
        rootViewController?.presentViewController(
            viewControllerToPresent = activityViewController,
            animated = true,
            completion = null
        )
    }

    private fun getRootViewController(): UIViewController? {
        val windows = UIApplication.sharedApplication.windows

        for (i in 0 until windows.size) {
            val window = windows[i] as? UIWindow
            if (window?.rootViewController != null) {
                return window.rootViewController
            }
        }
        return null
    }
}

actual fun getImageSharer(): ImageSharer {
    return ImageSharer()
}