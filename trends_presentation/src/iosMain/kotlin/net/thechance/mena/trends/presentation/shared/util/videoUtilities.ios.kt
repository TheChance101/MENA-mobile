package net.thechance.mena.trends.presentation.shared.util

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.AVFoundation.AVAssetImageGenerator
import platform.AVFoundation.AVURLAsset
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMake
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.posix.memcpy

actual fun getVideoUtilities(): VideoUtilities {
    return VideoUtilitiesImpl()
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class VideoUtilitiesImpl : VideoUtilities {

    override suspend fun getDuration(filePath: String): Long? {
        return withContext(Dispatchers.IO) {
            runCatching {
                val fileUrl = NSURL.URLWithString(filePath) ?: NSURL.fileURLWithPath(filePath)
                val asset = AVURLAsset.URLAssetWithURL(fileUrl, options = null)
                val duration = asset.duration
                (CMTimeGetSeconds(duration) * 1000).toLong().takeIf { it > 0 }
            }.getOrNull()
        }
    }

    override suspend fun extractVideoFrame(filePath: String, timeMs: Long): ByteArray? {
        return withContext(Dispatchers.IO) {
            runCatching {
                val fileUrl = NSURL.URLWithString(filePath) ?: NSURL.fileURLWithPath(filePath)
                fileUrl.startAccessingSecurityScopedResource()
                val asset = AVURLAsset.URLAssetWithURL(fileUrl, options = null)
                val imageGenerator = createAVAssetImageGenerator(asset)

                try {
                    val time = CMTimeMakeWithSeconds(timeMs / 1000.0, 600)
                    imageGenerator.copyCGImageAtTime(time, actualTime = null, error = null)
                        .let { cgImage ->
                            val uiImage = UIImage.imageWithCGImage(cgImage)
                            UIImageJPEGRepresentation(uiImage, 0.9)?.toByteArray()
                        }
                } finally {
                    fileUrl.stopAccessingSecurityScopedResource()
                    imageGenerator.cancelAllCGImageGeneration()
                }
            }.getOrNull()
        }
    }

    override suspend fun extractVideoFrame(filePath: String, percent: Float): ByteArray? {
        return withContext(Dispatchers.IO) {
            val duration = getDuration(filePath) ?: 1L
            val clamped = percent.coerceIn(0f, 1f)
            val targetTimeUs = (duration * clamped * 1000).toLong()
            runCatching {
                extractVideoFrame(filePath, targetTimeUs)
            }.getOrNull()
        }
    }

    private fun createAVAssetImageGenerator(asset: AVURLAsset): AVAssetImageGenerator {
        return AVAssetImageGenerator(asset = asset).apply {
            appliesPreferredTrackTransform = true
            requestedTimeToleranceBefore = CMTimeMake(0, 600)
            requestedTimeToleranceAfter = CMTimeMake(0, 600)
        }
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
}