package net.thechance.mena.faith.data.remote.network

import android.content.Context
import org.koin.mp.KoinPlatform.getKoin
import java.io.File
import java.io.FileOutputStream
import java.net.URL

actual suspend fun downloadFileIntoPrivateStorage(
    url: String,
    fileName: String,
    isZipFile: Boolean,
): String? {
    val context: Context = getKoin().get()
    return try {
        val file =
            File(context.filesDir, fileName) // context.filesDir points to private internal storage
        val connection = URL(url).openConnection()
        connection.connect()

        val inputStream = connection.getInputStream()
        val outputStream = FileOutputStream(file)

        inputStream.copyTo(outputStream)

        inputStream.close()
        outputStream.close()

        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun extractFile() {}
