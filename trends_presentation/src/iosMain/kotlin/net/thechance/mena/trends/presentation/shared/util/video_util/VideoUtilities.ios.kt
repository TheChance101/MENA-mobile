package net.thechance.mena.trends.presentation.shared.util.video_util

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.readValue
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.AVFoundation.AVAssetImageGenerator
import platform.AVFoundation.AVAssetImageGeneratorApertureModeEncodedPixels
import platform.AVFoundation.AVAssetImageGeneratorSucceeded
import platform.AVFoundation.AVURLAsset
import platform.AVFoundation.valueWithCMTime
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMake
import platform.CoreMedia.kCMTimeZero
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSValue
import platform.Foundation.create
import platform.Foundation.writeToURL
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.posix.memcpy
import kotlin.coroutines.resume

actual fun getVideoUtilities(): VideoUtilities {
    return VideoUtilitiesImpl()
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class VideoUtilitiesImpl : VideoUtilities {
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

    override suspend fun extractVideoFrame(
        videoData: ByteArray,
        timeMs: Long
    ): ByteArray? {
        return suspendCancellableCoroutine { continuation ->
            val tempURL =
                NSURL.fileURLWithPath(NSTemporaryDirectory() + "temp_video_frame_${timeMs}.mp4")

            try {
                val nsData = videoData.toNSData()
                if (!nsData.writeToURL(tempURL, atomically = true)) {
                    continuation.resumeWith(Result.success(null))
                    return@suspendCancellableCoroutine
                }

                val asset = AVURLAsset.URLAssetWithURL(URL = tempURL, options = null)
                val time = CMTimeMake(value = timeMs, timescale = 1000)
                val imageGenerator = AVAssetImageGenerator.assetImageGeneratorWithAsset(asset)

                imageGenerator.requestedTimeToleranceAfter = kCMTimeZero.readValue()
                imageGenerator.requestedTimeToleranceBefore = kCMTimeZero.readValue()
                imageGenerator.appliesPreferredTrackTransform = true
                imageGenerator.apertureMode = AVAssetImageGeneratorApertureModeEncodedPixels

                imageGenerator.generateCGImagesAsynchronouslyForTimes(
                    requestedTimes = listOf(NSValue.valueWithCMTime(time))
                ) { _, imageRef, _, result, error ->
                    NSFileManager.defaultManager.removeItemAtURL(tempURL, error = null)

                    if (continuation.isCompleted) return@generateCGImagesAsynchronouslyForTimes

                    val byteArray = when {
                        error != null || imageRef == null || result != AVAssetImageGeneratorSucceeded -> null
                        else -> {
                            val uiImage = UIImage.imageWithCGImage(imageRef)
                            UIImageJPEGRepresentation(
                                uiImage,
                                compressionQuality = 0.9
                            )?.toByteArray()
                        }
                    }

                    continuation.resume(byteArray)
                }

                continuation.invokeOnCancellation {
                    imageGenerator.cancelAllCGImageGeneration()
                    NSFileManager.defaultManager.removeItemAtURL(tempURL, error = null)
                }

            } catch (e: Exception) {
                NSFileManager.defaultManager.removeItemAtURL(tempURL, error = null)
                continuation.resumeWith(Result.success(null))
            }
        }
    }

    override suspend fun extractVideoFrame(
        videoData: ByteArray, percent: Float
    ): ByteArray? = withContext(Dispatchers.IO) {

        val duration = getDuration(videoData) ?: 1L
        val clamped = percent.coerceIn(0f, 1f)
        val targetTimeUs = (duration * clamped * 1000).toLong()

        return@withContext runCatching {
            extractVideoFrame(
                videoData, targetTimeUs
            )
        }.getOrNull()
    }

    private fun NSData.toByteArray(): ByteArray {
        return ByteArray(this.length.toInt()).apply {
            usePinned { pinned ->
                memcpy(
                    pinned.addressOf(0), this@toByteArray.bytes, this@toByteArray.length
                )
            }
        }
    }

    private fun ByteArray.toNSData(): NSData {
        return this.usePinned { pinned ->
            NSData.create(
                bytes = pinned.addressOf(0), length = this.size.toULong()
            )
        }
    }
}