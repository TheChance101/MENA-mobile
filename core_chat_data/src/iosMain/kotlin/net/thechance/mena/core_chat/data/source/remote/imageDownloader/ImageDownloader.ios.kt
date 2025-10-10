package net.thechance.mena.core_chat.data.source.remote.imageDownloader

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.Photos.PHPhotoLibrary
import platform.UIKit.UIImage
import platform.UIKit.UIImageWriteToSavedPhotosAlbum

@OptIn(ExperimentalForeignApi::class)

actual suspend fun downloadImageToGalleryPlatformSpecific(url: String): Boolean {
    return withContext(Dispatchers.Default) {
        try {
            val nsUrl = NSURL(string = url)
            val data = NSData.dataWithContentsOfURL(nsUrl) ?: return@withContext false
            val image = UIImage(data = data)

            var success = false
            PHPhotoLibrary.sharedPhotoLibrary().performChanges({
                UIImageWriteToSavedPhotosAlbum(image, null, null, null)
                success = true
            }, completionHandler = null)

            success
        } catch (e: Exception) {
            println("❌ Error saving image: ${e.message}")
            false
        }
    }
}