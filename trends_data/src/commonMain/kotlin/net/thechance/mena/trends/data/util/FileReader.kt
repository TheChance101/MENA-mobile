package net.thechance.mena.trends.data.util

import kotlinx.io.RawSource

expect fun getPlatformFileReader(): FileReader

interface FileReader {
    fun readFile(filePath: String): RawSource
}