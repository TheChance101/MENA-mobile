package net.thechance.mena.identity.data.dataSource.local.storage

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.pin
import net.thechance.mena.identity.data.dataSource.exception.FileNotSavedException
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.create
import platform.Photos.PHAssetChangeRequest
import platform.Photos.PHPhotoLibrary
import platform.UIKit.UIImage

actual class ImagesGalleryManager {

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual suspend fun saveImage(imageBytes: ByteArray) {
        val data = NSData.create(
            bytes = imageBytes.pin().addressOf(0),
            length = imageBytes.size.toULong()
        )
        val uiImage = UIImage(data)

        PHPhotoLibrary.sharedPhotoLibrary().performChanges(
            changeBlock = { changeBlock(uiImage) },
            completionHandler = ::completionHandler
        )
    }

    private fun changeBlock(uiImage: UIImage) {
        PHAssetChangeRequest.creationRequestForAssetFromImage(uiImage)
    }

    private fun completionHandler(isSaved: Boolean, error: NSError?) {
        if (!isSaved) throw FileNotSavedException(error?.description ?: "")
    }
}