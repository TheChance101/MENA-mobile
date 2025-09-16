package net.thechance.mena.dukan.presentation.util.imageCrop

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.ByteArrayOutputStream

actual fun ImageBitmap.toPngByteArray(): ByteArray {
    val bitmap = this.asAndroidBitmap()
    val stream = ByteArrayOutputStream()
    bitmap.compress(
        Bitmap.CompressFormat.PNG,
        100,
        stream
    )
    return stream.toByteArray()
}