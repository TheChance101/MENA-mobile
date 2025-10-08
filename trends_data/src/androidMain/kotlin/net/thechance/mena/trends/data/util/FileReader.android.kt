package net.thechance.mena.trends.data.util

import android.content.Context
import androidx.core.net.toUri
import kotlinx.io.RawSource
import kotlinx.io.asSource
import org.koin.java.KoinJavaComponent.getKoin

actual fun getPlatformFileReader(): FileReader = FileReaderImpl(getKoin().get<Context>())

class FileReaderImpl(
    private val context: Context
): FileReader {

    override fun readFile(filePath: String): RawSource {
        return context
            .contentResolver
            .openInputStream(filePath.toUri())
            ?.asSource()
            ?: throw Exception("Failed to open input stream")
    }
}