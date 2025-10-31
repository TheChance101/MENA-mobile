package net.thechance.mena.trends.data.repository.util

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.asSource
import kotlinx.io.RawSource
import net.thechance.mena.trends.data.util.VideoFileHandler

class VideoFileHandlerMock: VideoFileHandler {

    override fun readFile(filePath: String): RawSource {
        return ByteReadChannel(byteArrayOf(1, 2, 3)).asSource()
    }

    override suspend fun getMimeType(filePath: String): String {
        return "video/mp4"
    }

    override suspend fun getDuration(filePath: String): Long? {
        return 1000L
    }

    override suspend fun extractVideoFrame(filePath: String, timeMs: Long): ByteArray? {
        return byteArrayOf(1, 2)
    }
}