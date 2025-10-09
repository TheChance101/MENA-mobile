package net.thechance.mena.trends.data.repository.util

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.asSource
import kotlinx.io.RawSource
import net.thechance.mena.trends.data.util.FileReader

class FileReaderMock: FileReader {
    override fun readFile(filePath: String): RawSource {
        return ByteReadChannel(byteArrayOf(1, 2, 3)).asSource()
    }
}