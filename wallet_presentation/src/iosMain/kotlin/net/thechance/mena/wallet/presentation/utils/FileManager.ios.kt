package net.thechance.mena.wallet.presentation.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.refTo
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import kotlinx.io.files.FileNotFoundException
import org.koin.core.annotation.Single
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.dataWithBytes
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.writeToFile
import platform.posix.memcpy
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Single
actual class FileManagerImpl : FileManager {
    actual override suspend fun saveFile(
        data: ByteArray,
        location: StorageLocation,
        mimeType: String
    ): String = io {
        when (location) {
            is StorageLocation.Cache -> saveToCache(data, location.fileName)
            is StorageLocation.Downloads -> saveToDocuments(data, location.fileName)
        }
    }

    actual override suspend fun readFile(location: StorageLocation): ByteArray = io {
        val path = getFilePath(location)

        val nsData = NSData.dataWithContentsOfFile(path)
            ?: throw FileNotFoundException("File not found: $path")

        nsData.toByteArray()
    }

    @OptIn(ExperimentalForeignApi::class)
    actual override suspend fun deleteFile(location: StorageLocation) = io {
        val fileManager = NSFileManager.defaultManager
        val path = getFilePath(location)

        if (fileManager.fileExistsAtPath(path)) {
            fileManager.removeItemAtPath(path, error = null)
        }
    }

    actual override suspend fun checkIfFileExists(location: StorageLocation): Boolean = io {
        val fileManager = NSFileManager.defaultManager
        val path = getFilePath(location)

        fileManager.fileExistsAtPath(path)
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun saveToCache(bytes: ByteArray, name: String): String {
        val tempDir = NSTemporaryDirectory()
        val filePath = tempDir + name
        val saved = bytes.usePinned {
            val nsData = NSData.dataWithBytes(it.addressOf(0), bytes.size.toULong())
            nsData.writeToFile(filePath, true)
        }
        val url = if (saved) NSURL.fileURLWithPath(filePath) else null
        return url?.path ?: throw IOException("Failed to save to cache")
    }

    @OptIn(ExperimentalForeignApi::class, ExperimentalTime::class)
    private fun saveToDocuments(
        pdfData: ByteArray,
        fileName: String
    ): String {
        val uniqueFileName = fileName

        val fileManager = NSFileManager.defaultManager
        val documentsPath = getDocumentsDirectory()
        val appFolderPath = "$documentsPath/$APP_DOWNLOADS_FOLDER"

        if (!fileManager.fileExistsAtPath(appFolderPath)) {
            val created = fileManager.createDirectoryAtPath(
                appFolderPath,
                withIntermediateDirectories = true,
                attributes = null,
                error = null
            )
            if (!created) {
                throw IOException("Failed to create directory")
            }
        }

        val filePath = "$appFolderPath/$uniqueFileName"

        val saved = pdfData.usePinned { pinned ->
            val nsData = NSData.dataWithBytes(
                pinned.addressOf(0),
                pdfData.size.toULong()
            )
            nsData.writeToFile(filePath, atomically = true)
        }

        if (!saved) {
            throw IOException("Failed to save file")
        }

        return "$APP_DOWNLOADS_FOLDER/$uniqueFileName"
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun getDocumentsDirectory(): String {
        val paths = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true
        )
        return paths.first() as String
    }

    private fun getFilePath(location: StorageLocation): String {
        return when (location) {
            is StorageLocation.Cache -> {
                val tempDir = NSTemporaryDirectory()
                "$tempDir${location.fileName}"
            }

            is StorageLocation.Downloads -> {
                val documentsPath = getDocumentsDirectory()
                "$documentsPath/${APP_DOWNLOADS_FOLDER}/${location.fileName}"
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun NSData.toByteArray(): ByteArray {
        val length = length.toInt()
        val byteArray = ByteArray(length)
        if (length > 0) {
            memcpy(byteArray.refTo(0), bytes, length.convert())
        }
        return byteArray
    }

    private suspend fun <T> io(block: () -> T): T = withContext(Dispatchers.IO) { block() }

    private companion object {
        const val APP_DOWNLOADS_FOLDER = "MENA"
    }
}