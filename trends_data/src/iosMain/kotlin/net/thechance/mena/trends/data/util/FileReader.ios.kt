package net.thechance.mena.trends.data.util

import kotlinx.io.RawSource
import kotlinx.io.asSource
import platform.Foundation.NSInputStream
import platform.Foundation.NSURL

actual fun getPlatformFileReader(): FileReader = FileReaderImpl()

class FileReaderImpl : FileReader {

    override fun readFile(filePath: String): RawSource {
        val fileUrl = NSURL.URLWithString(filePath) ?: NSURL.fileURLWithPath(filePath)
        return NSInputStream(fileUrl).asSource()
    }
}