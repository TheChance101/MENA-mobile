package net.thechance.mena.trends.presentation.shared.util

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AVFoundation.*
import platform.CoreMedia.CMTimeGetSeconds
import platform.Foundation.*
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class)
actual suspend fun getVideoDuration(videoBytes: ByteArray): Long? {
    return suspendCancellableCoroutine { continuation ->
        try {
            val nsData = videoBytes.toNSData()
            val tempURL = NSURL.fileURLWithPath(NSTemporaryDirectory() + "temp_video.mp4")

            nsData.writeToURL(tempURL, true)
            val asset = AVURLAsset.URLAssetWithURL(tempURL, null)

            asset.loadValuesAsynchronouslyForKeys(listOf("duration")) {
                val duration = asset.duration
                val durationInMillis = (CMTimeGetSeconds(duration) * 1000).toLong()

                NSFileManager.defaultManager.removeItemAtURL(tempURL, null)

                continuation.resume(if (durationInMillis > 0) durationInMillis else null)
            }
        } catch (e: Exception) {
            continuation.resume(null)
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
fun ByteArray.toNSData(): NSData {
    return NSData.create(
        bytes = this.refTo(0) as COpaquePointer?,
        length = this.size.toULong()
    )
}