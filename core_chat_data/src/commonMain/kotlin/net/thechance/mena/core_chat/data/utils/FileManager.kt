package net.thechance.mena.core_chat.data.utils

interface FileManager {
    fun getCacheDirectory(subdirectory: String? = null): String
    fun createDirectory(path: String): Boolean
    suspend fun writeFile(path: String, bytes: ByteArray): Boolean
    fun isFileExists(path: String): Boolean
    fun getFileSize(path: String): Long
}

expect fun createFileManager() : FileManager