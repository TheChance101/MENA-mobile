package net.thechance.mena.trends.data.util

import kotlinx.io.RawSource

expect fun getPlatformFileReader(): VideoFileHandler

interface VideoFileHandler {
    fun readFile(filePath: String): RawSource
    suspend fun getDuration(filePath: String): Long?
    suspend fun getMimeType(filePath: String): String
    suspend fun extractVideoFrame(filePath: String, timeMs: Long = 0L): ByteArray?
}