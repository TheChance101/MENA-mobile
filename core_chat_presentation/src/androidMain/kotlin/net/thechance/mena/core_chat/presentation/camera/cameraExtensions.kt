package net.thechance.mena.core_chat.presentation.camera

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.InputStream


internal fun Uri.getBitmapFromUri(contentResolver: ContentResolver): Bitmap? {
    return runCatching {
        val inputStreamForBitmap = contentResolver.openInputStream(this)
        val bitmap = inputStreamForBitmap?.use {
            BitmapFactory.decodeStream(it)
        }

        val inputStreamForExif = contentResolver.openInputStream(this)
        val rotatedBitmap = bitmap?.rotateImageIfRequired(inputStreamForExif)

        rotatedBitmap
    }.getOrElse {
        null
    }
}

internal fun Bitmap.rotateImageIfRequired(inputStream: InputStream?): Bitmap {
    if (inputStream == null) return this
    return runCatching {
        val exif = ExifInterface(inputStream)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }

        Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }.getOrElse {
        this
    }
}

fun Bitmap?.toByteArray(): ByteArray? {
    return this?.let {
        var quality = 100
        val stream = ByteArrayOutputStream()
        it.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        var bytes = stream.toByteArray()

        while (bytes.size > 5_000_000 && quality > 10) {
            quality -= 10
            stream.reset()
            it.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            bytes = stream.toByteArray()
        }

        bytes
    }
}
