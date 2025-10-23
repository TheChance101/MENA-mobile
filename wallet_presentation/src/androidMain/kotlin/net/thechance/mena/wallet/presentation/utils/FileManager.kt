package net.thechance.mena.wallet.presentation.utils

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import org.koin.core.context.GlobalContext
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

@Single
actual class FileManagerImpl : FileManager {
    private val context = GlobalContext.get().get<Context>()

    actual override suspend fun saveFile(
        data: ByteArray,
        location: StorageLocation,
        mimeType: String
    ): String = io {
        when (location) {
            is StorageLocation.Cache -> saveToCache(data, location.fileName)
            is StorageLocation.Downloads -> saveToDownloads(data, location.fileName, mimeType)
        }
    }

    actual override suspend fun readFile(location: StorageLocation): ByteArray = io {
        when (location) {
            is StorageLocation.Cache -> readFromCache(location.fileName)
            is StorageLocation.Downloads -> readFromDownloads(location.fileName)
        }
    }

    actual override suspend fun deleteFile(location: StorageLocation) = io {
        when (location) {
            is StorageLocation.Cache -> deleteFromCache(location.fileName)
            is StorageLocation.Downloads -> deleteFromDownloads(location.fileName)
        }
    }

    actual override suspend fun checkIfFileExists(location: StorageLocation): Boolean = io {
            when (location) {
                is StorageLocation.Cache -> checkIfCacheFileExists(location.fileName)
                is StorageLocation.Downloads -> checkIfDownloadFileExists(location.fileName)
            }
        }

    private fun saveToCache(data: ByteArray, fileName: String): String {
        val file = File(context.cacheDir, fileName)
        file.writeBytes(data)
        return file.absolutePath
    }

    private fun saveToDownloads(
        data: ByteArray,
        fileName: String,
        mimeType: String
    ): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveToMediaStore(fileName, data, mimeType)
        } else {
            saveToLegacyStorage(fileName, data)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveToMediaStore(fileName: String, data: ByteArray, mimeType: String): String {
        val resolver = context.contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, "$DOWNLOAD_DIR_BASE/$APP_DOWNLOADS_FOLDER")
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            ?: throw IOException("Failed to create MediaStore entry")

        resolver.openOutputStream(uri)?.use { it.write(data) }
            ?: throw IOException("Failed to open output stream")

        val localizedDownloads = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS
        ).name

        return "$localizedDownloads/$APP_DOWNLOADS_FOLDER/$fileName"
    }

    @Suppress("DEPRECATION")
    private fun saveToLegacyStorage(fileName: String, data: ByteArray): String {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS
        )
        val appFolder = File(downloadsDir, APP_DOWNLOADS_FOLDER)

        if (!appFolder.exists() && !appFolder.mkdirs()) {
            throw IOException("Failed to create downloads folder")
        }

        val file = File(appFolder, fileName)
        file.writeBytes(data)
        return file.absolutePath
    }

    private fun readFromCache(fileName: String): ByteArray {
        val file = File(context.cacheDir, fileName)
        if (!file.exists()) throw FileNotFoundException("File not found in cache: $fileName")
        return file.readBytes()
    }

    private fun readFromDownloads(fileName: String): ByteArray {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            readFromMediaStore(fileName)
        } else {
            readFromLegacyStorage(fileName)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun readFromMediaStore(fileName: String): ByteArray {
        val resolver = context.contentResolver
        val collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.MediaColumns._ID)
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(fileName)

        resolver.query(collection, projection, selection, selectionArgs, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                val uri = ContentUris.withAppendedId(collection, id)
                return resolver.openInputStream(uri)?.use { it.readBytes() }
                    ?: throw IOException("Failed to open input stream")
            }
        }
        throw FileNotFoundException("File not found in MediaStore: $fileName")
    }

    @Suppress("DEPRECATION")
    private fun readFromLegacyStorage(fileName: String): ByteArray {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS
        )
        val file = File(File(downloadsDir, APP_DOWNLOADS_FOLDER), fileName)
        if (!file.exists()) throw FileNotFoundException("File not found: ${file.absolutePath}")
        return file.readBytes()
    }

    private fun deleteFromCache(fileName: String) {
        val file = File(context.cacheDir, fileName)
        if(file.exists()) file.delete()
    }

    private fun deleteFromDownloads(fileName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deleteFromMediaStore(fileName)
        } else {
            deleteFromLegacyStorage(fileName)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun deleteFromMediaStore(fileName: String) {
        val resolver = context.contentResolver
        val collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(fileName)

        resolver.delete(collection, selection, selectionArgs)
    }

    @Suppress("DEPRECATION")
    private fun deleteFromLegacyStorage(fileName: String) {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS
        )
        val file = File(File(downloadsDir, APP_DOWNLOADS_FOLDER), fileName)
        if (file.exists()) file.delete()
    }

    private fun checkIfCacheFileExists(fileName: String): Boolean {
        val file = File(context.cacheDir, fileName)
        return file.exists()
    }

    private fun checkIfDownloadFileExists(fileName: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkIfMediaStoreFileExists(fileName)
        } else {
            checkIfLegacyStorageFileExists(fileName)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkIfMediaStoreFileExists(fileName: String): Boolean {
        val resolver = context.contentResolver
        val collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.MediaColumns._ID)
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(fileName)

        return resolver.query(collection, projection, selection, selectionArgs, null)
            ?.use { cursor ->
                cursor.moveToFirst()
            } ?: false
    }

    private fun checkIfLegacyStorageFileExists(fileName: String): Boolean {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS
        )
        val file = File(File(downloadsDir, APP_DOWNLOADS_FOLDER), fileName)
        return file.exists()
    }

    private suspend fun <T> io(block: () -> T): T = withContext(Dispatchers.IO) { block() }

    private companion object {
        const val DOWNLOAD_DIR_BASE = "Download"
        const val APP_DOWNLOADS_FOLDER = "MENA"
    }
}
