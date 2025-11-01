package net.thechance.mena.faith.data.remote.network

import android.content.Context
import org.koin.mp.KoinPlatform.getKoin
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URL
import java.util.zip.ZipInputStream

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

        // Extract ZIP
        val extractedDir = File(context.filesDir, "extracted")
        if (!extractedDir.exists()) extractedDir.mkdirs()

        unzip(file, extractedDir)

        // Return extracted path
        return extractedDir.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun unzip(
    zipFile: File,
    targetDir: File,
) {
    ZipInputStream(FileInputStream(zipFile)).use { zipStream ->
        var entry = zipStream.nextEntry
        while (entry != null) {
            val outFile = File(targetDir, entry.name)
            if (entry.isDirectory) {
                outFile.mkdirs()
            } else {
                outFile.parentFile?.mkdirs()
                FileOutputStream(outFile).use { out ->
                    zipStream.copyTo(out)
                }
            }
            entry = zipStream.nextEntry
        }
    }
}
