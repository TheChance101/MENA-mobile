package net.thechance.mena.identity.presentation.screen.imageCropper

import org.jetbrains.compose.resources.StringResource

data class ImageCropperScreenState(
    val imageByteArray: ByteArray? = null,
    val errorMessage: StringResource? = null
)