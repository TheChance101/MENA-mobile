package net.thechance.mena.trends.data.util

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
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
import platform.Foundation.inputStreamWithFileAtPath
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UniformTypeIdentifiers.UTType
import platform.posix.memcpy

actual fun getPlatformFileReader(): VideoFileHandler = VideoFileHandlerImpl()

@OptIn(ExperimentalForeignApi::class)
class VideoFileHandlerImpl : VideoFileHandler {

    @OptIn(ExperimentalForeignApi::class)
    override fun readFile(filePath: String): RawSource {
        val inputStream = NSInputStream.inputStreamWithFileAtPath(filePath)
            ?: throw IllegalStateException("Cannot create input stream for file: $filePath")

        inputStream.open()
        return inputStream.asSource()
    }

    override suspend fun getDuration(filePath: String): Long? {
        return runCatching {
            val fileUrl = NSURL.URLWithString(filePath) ?: NSURL.fileURLWithPath(filePath)
            val asset = AVURLAsset.URLAssetWithURL(fileUrl, options = null)
            val duration = asset.duration
            (CMTimeGetSeconds(duration) * ONE_SECOND).toLong()
                .takeIf { duration -> duration > 0 }
        }.getOrNull()
    }

    override suspend fun getMimeType(filePath: String): String {
        return runCatching {
            val fileUrl = NSURL.URLWithString(filePath) ?: NSURL.fileURLWithPath(filePath)
            fileUrl.pathExtension?.let { extension ->
                UTType.typeWithFilenameExtension(extension)?.preferredMIMEType
            } ?: DEFAULT_MIME_TYPE
        }.getOrDefault(DEFAULT_MIME_TYPE)
    }

    override suspend fun extractVideoFrame(filePath: String, timeMs: Long): ByteArray? {
        return runCatching {
            val fileUrl = NSURL.fileURLWithPath(filePath)
            val asset = AVURLAsset.URLAssetWithURL(fileUrl, options = null)
            val imageGenerator = createAVAssetImageGenerator(asset)

            try {
                val time = CMTimeMakeWithSeconds(timeMs / ONE_SECOND, TIME_SCALE)
                imageGenerator.copyCGImageAtTime(time, actualTime = null, error = null)
                    .let { cgImage ->
                        val uiImage = UIImage.imageWithCGImage(cgImage)
                        UIImageJPEGRepresentation(uiImage, COMPRESS_QUALITY)?.toByteArray()
                    }
            } finally {
                imageGenerator.cancelAllCGImageGeneration()
            }
        }.getOrNull()
    }

    private fun createAVAssetImageGenerator(asset: AVURLAsset): AVAssetImageGenerator {
        return AVAssetImageGenerator(asset = asset).apply {
            appliesPreferredTrackTransform = true
            requestedTimeToleranceBefore = CMTimeMake(0, TIME_SCALE)
            requestedTimeToleranceAfter = CMTimeMake(0, TIME_SCALE)
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

    private companion object {
        const val ONE_SECOND = 1000.0
        const val TIME_SCALE = 600
        const val COMPRESS_QUALITY = 1.0
        const val DEFAULT_MIME_TYPE = "application/octet-stream"
    }
}