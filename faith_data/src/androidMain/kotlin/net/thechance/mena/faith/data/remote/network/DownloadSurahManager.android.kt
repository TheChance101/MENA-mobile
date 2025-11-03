package net.thechance.mena.faith.data.remote.network

import android.content.Context
import org.koin.mp.KoinPlatform.getKoin
import java.io.File
import java.io.FileOutputStream
import java.net.URL

actual suspend fun downloadSurahFileToAppStorage(
    url: String,
    fileName: String,
): String? {
    val context: Context = getKoin().get()
    return try {
        val file = File(context.filesDir, fileName)

        file.parentFile?.mkdirs()

        val connection = URL(url).openConnection()
        connection.connectTimeout = 30_000
        connection.readTimeout = 30_000
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
