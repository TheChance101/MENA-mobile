package net.thechance.mena.wallet.presentation.utils

import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.Foundation.NSURL
@Single
@OptIn(ExperimentalForeignApi::class)
actual class FileSharerImpl actual constructor(@Provided private val fileManager: FileManager) : FileSharer {
    actual override suspend fun shareFile(
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String,
        shareTitle: String
    ) {
        val tempPath = fileManager.saveFile(fileBytes, StorageLocation.Cache(fileName), mimeType)
        val fileURL = NSURL.fileURLWithPath(tempPath)
        val activityViewController = UIActivityViewController(listOf(fileURL), null)
        UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
            activityViewController, animated = true, completion = null
        )
    }
}