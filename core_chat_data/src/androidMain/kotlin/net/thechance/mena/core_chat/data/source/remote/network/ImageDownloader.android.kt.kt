package net.thechance.mena.core_chat.data.source.remote.network

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import org.koin.mp.KoinPlatform.getKoin
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

actual suspend fun downloadImageToGalleryPlatformSpecific(url: String): Boolean {
    val context: Context = getKoin().get()

    return try {

        val connection = URL(url).openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val input: InputStream = connection.inputStream
        val bitmap: Bitmap = BitmapFactory.decodeStream(input)


        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Mena")
        }

        val uri =
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: return false

        context.contentResolver.openOutputStream(uri)?.use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }

        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

