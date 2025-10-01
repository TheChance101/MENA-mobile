package net.thechance.mena.trends.presentation.shared.util.video_util

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.AVFoundation.AVAssetImageGenerator
import platform.AVFoundation.AVURLAsset
import platform.CoreGraphics.CGImageRelease
import platform.CoreMedia.CMTime
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMake
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
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
    ): ByteArray? = withContext(Dispatchers.IO) {
        runCatching {
            useTempFile { tempFileUrl, filePath ->
                videoData.toNSData().writeToURL(tempFileUrl, atomically = true)

                val asset = AVURLAsset(uRL = tempFileUrl)
                val imageGenerator = AVAssetImageGenerator(asset).apply {
                    appliesPreferredTrackTransform = true
                    requestedTimeToleranceBefore = CMTimeMake(0, 1)
                    requestedTimeToleranceAfter = CMTimeMake(0, 1)
                }

                val time = CMTimeMake(timeMs, 1000)

                memScoped {
                    val actualTimePtr = alloc<CMTime>()
                    val errorPtr = alloc<ObjCObjectVar<NSError?>>()

                    val cgImage = imageGenerator.copyCGImageAtTime(
                        time,
                        actualTimePtr.ptr,
                        errorPtr.ptr
                    )!!

                    try {
                        val uiImage = UIImage.imageWithCGImage(cgImage)
                        val imageData = UIImageJPEGRepresentation(uiImage, 0.9)!!
                        imageData.toByteArray()
                    } finally {
                        CGImageRelease(cgImage)
                    }
                }
            }
        }.getOrNull()
    }

    override suspend fun extractVideoFrame(
        videoData: ByteArray, percent: Float
    ): ByteArray? = withContext(Dispatchers.IO) {

        val duration = getDuration(videoData) ?: 1L
        val clamped = percent.coerceIn(0f, 1f)
        val targetTimeUs = (duration * clamped * 1000).toLong()

        return@withContext runCatching {
            extractVideoFrame(
                videoData,
                targetTimeUs
            )
        }.getOrNull()
    }

    private fun NSData.toByteArray(): ByteArray {
        return ByteArray(this.length.toInt()).apply {
            usePinned { pinned ->
                memcpy(
                    pinned.addressOf(0),
                    this@toByteArray.bytes,
                    this@toByteArray.length
                )
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

private inline fun <R> useTempFile(
    prefix: String = "video_",
    suffix: String = ".tmp",
    block: (NSURL, String) -> R
): R {
    val tempDir = NSTemporaryDirectory()
    val fileName = "$prefix${NSUUID.UUID().UUIDString()}$suffix"
    val filePath = "$tempDir$fileName"
    val tempFileUrl = NSURL.fileURLWithPath(filePath)

    try {
        return block(tempFileUrl, filePath)
    } finally {
        try {
            val fileManager = NSFileManager.defaultManager
            if (fileManager.fileExistsAtPath(filePath)) {
                fileManager.removeItemAtPath(filePath, null)
            }
        } catch (e: Exception) {
            println("Warning: Failed to delete temp file - ${e.message}")
        }
    }
}