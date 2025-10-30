package net.thechance.mena.identity.presentation.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.decodeToImageBitmap
import io.github.vinceglb.filekit.dialogs.compose.util.encodeToByteArray

class ImageDecoderImpl : ImageDecoder{

    override fun decodeImage(byteArray: ByteArray): ImageBitmap {
        return byteArray.decodeToImageBitmap()
    }

    override suspend fun encodeImage(imageBitmap: ImageBitmap): ByteArray {
        return imageBitmap.encodeToByteArray()
    }

}