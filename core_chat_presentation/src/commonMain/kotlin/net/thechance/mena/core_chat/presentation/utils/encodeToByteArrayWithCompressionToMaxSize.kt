package net.thechance.mena.core_chat.presentation.utils

import androidx.compose.ui.graphics.ImageBitmap
import io.github.vinceglb.filekit.dialogs.compose.util.encodeToByteArray


suspend fun ImageBitmap.encodeToByteArrayWithCompressionToMaxSize(
    maxSize: Long = 5_000_000,
    step: Int = 1
): ByteArray {
    var quality = 100
    var byteArray = encodeToByteArray(quality = quality)

    var steps = 0
    while (byteArray.size > maxSize && quality > step) {
        quality -= step
        byteArray = encodeToByteArray(quality = quality)
        steps += 1
    }

    return byteArray
}