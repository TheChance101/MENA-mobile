@file:OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)

package net.thechance.mena.core_chat.data.utils

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSFileSize
import platform.Foundation.NSNumber
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.writeToFile

actual fun createFileManager(): FileManager {
    return IosFileManager()
}

class IosFileManager : FileManager {

    private val fileManager = NSFileManager.defaultManager

    override fun getCacheDirectory(subdirectory: String?): String {
        val cacheDir = fileManager.URLsForDirectory(
            NSCachesDirectory,
            NSUserDomainMask
        ).firstOrNull() as? NSURL

        val baseDir = cacheDir?.path ?: ""

        return if (subdirectory != null) {
            val fullPath = cacheDir?.URLByAppendingPathComponent(subdirectory)?.path ?: ""
            createDirectory(fullPath)
            fullPath
        } else {
            baseDir
        }
    }

    override fun createDirectory(path: String): Boolean {
        return try {
            if (!isFileExists(path)) {
                fileManager.createDirectoryAtPath(
                    path = path,
                    withIntermediateDirectories = true,
                    attributes = null,
                    error = null
                )
            } else {
                true
            }
        } catch (_: Exception) {
            false
        }
    }

    override suspend fun writeFile(path: String, bytes: ByteArray): Boolean {
        return try {
            val data = bytes.toNSData()
            data.writeToFile(path, atomically = true)
        } catch (_: Exception) {
            false
        }
    }

    override fun isFileExists(path: String): Boolean {
        return fileManager.fileExistsAtPath(path)
    }

    override fun getFileSize(path: String): Long {
        val attributes = fileManager.attributesOfItemAtPath(path, error = null)
        return (attributes?.get(NSFileSize) as? NSNumber)?.longValue ?: 0L
    }

    private fun ByteArray.toNSData(): NSData {
        if (isEmpty()) {
            return NSData()
        }

        return usePinned { pinned ->
            NSData.create(
                bytes = pinned.addressOf(0),
                length = this.size.toULong()
            )
        }
    }
}