package net.thechance.mena.trends.data.util

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.io.RawSource
import kotlinx.io.asSource
import platform.AVFoundation.AVAssetImageGenerator
import platform.AVFoundation.AVURLAsset
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMake
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSData
import platform.Foundation.NSInputStream
import platform.Foundation.NSURL
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.posix.memcpy

actual fun getPlatformFileReader(): VideoFileHandler = VideoFileHandlerImpl()

class VideoFileHandlerImpl : VideoFileHandler {

    override suspend fun readFile(filePath: String): RawSource {
        return withContext(Dispatchers.IO) {
            val fileUrl = NSURL.URLWithString(filePath) ?: NSURL.fileURLWithPath(filePath)
            NSInputStream(fileUrl).asSource()
        }
    }

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
                    imageGenerator.cancelAllCGImageGeneration()
                }
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