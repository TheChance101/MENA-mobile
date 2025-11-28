@file:OptIn(BetaInteropApi::class)

package net.thechance.mena.faith.presentation.utils.extentions

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import platform.Foundation.NSData
import platform.Foundation.create
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
actual fun ImageBitmap.toByteArray(): ByteArray {
    val skiaBitmap = this.asSkiaBitmap()
    val skiaImage = Image.makeFromBitmap(skiaBitmap)

    val skiaData = skiaImage.encodeToData(EncodedImageFormat.PNG)
        ?: return ByteArray(0)

    val byteArray = skiaData.bytes

    val nsData = byteArray.usePinned { pinned ->
        NSData.create(
            bytes = pinned.addressOf(0),
            length = byteArray.size.toULong()
        )
    }

    return nsData.toByteArray()
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val size = this.length.toInt()
    val array = ByteArray(size)

    array.usePinned { pinned ->
        memcpy(
            pinned.addressOf(0),
            this.bytes,
            size.convert()
        )
    }

    return array
}
