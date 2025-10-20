package net.thechance.mena.core_chat.data.source.remote.network


import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.Photos.PHAssetCreationRequest
import platform.Photos.PHAssetResourceTypePhoto
import platform.Photos.PHPhotoLibrary
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.darwin.DISPATCH_TIME_FOREVER
import platform.darwin.dispatch_semaphore_create
import platform.darwin.dispatch_semaphore_signal
import platform.darwin.dispatch_semaphore_wait


actual suspend fun downloadImageToGalleryPlatformSpecific(url: String): Boolean {
    return try {
        val nsUrl = NSURL.URLWithString(url) ?: return false
        val data = NSData.dataWithContentsOfURL(nsUrl) ?: return false
        val image = UIImage(data = data)

        val jpegData = UIImageJPEGRepresentation(image, 1.0) ?: return false

        var success = false

        val semaphore = dispatch_semaphore_create(0)

        PHPhotoLibrary.sharedPhotoLibrary().performChanges({
            val request = PHAssetCreationRequest.creationRequestForAsset()
            request.addResourceWithType(
                type = PHAssetResourceTypePhoto,
                data = jpegData,
                options = null
            )
        }, completionHandler = { ok, error ->
            if (ok) {
                success = true
            } else {
                println("❌ Save failed: ${error?.localizedDescription}")
            }
            dispatch_semaphore_signal(semaphore)
        })

        dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER)

        success
    } catch (e: Exception) {
        println("❌ iOS save failed: $e")
        false
    }
}