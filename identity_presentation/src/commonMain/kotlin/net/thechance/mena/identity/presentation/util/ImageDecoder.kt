package net.thechance.mena.identity.presentation.utils

import androidx.compose.ui.graphics.ImageBitmap

interface ImageDecoder {

    fun decodeImage(byteArray: ByteArray): ImageBitmap

    suspend fun encodeImage(imageBitmap: ImageBitmap): ByteArray

}