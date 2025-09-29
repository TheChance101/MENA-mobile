package net.thechance.mena.trends.presentation.shared.util.video_util

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AVFoundation.AVURLAsset
import platform.CoreMedia.CMTimeGetSeconds
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.create
import platform.Foundation.writeToURL
import kotlin.coroutines.resume

actual fun getVideoDurationExtractor(): VideoDurationExtractor {
    return VideoDurationExtractorImpl()
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class VideoDurationExtractorImpl : VideoDurationExtractor {
    override suspend fun getDuration(videoBytes: ByteArray): Long? {
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

    private fun ByteArray.toNSData(): NSData {
        return this.usePinned { pinned ->
            NSData.create(
                bytes = pinned.addressOf(0),
                length = this.size.toULong()
            )
        }
    }
}