package net.thechance.mena.identity.presentation.screen.imageCropper

import androidx.compose.ui.graphics.ImageBitmap
import org.jetbrains.compose.resources.StringResource

data class ImageCropperScreenState(
    val imageBitmap: ImageBitmap? = null,
    val errorMessage: StringResource? = null
)