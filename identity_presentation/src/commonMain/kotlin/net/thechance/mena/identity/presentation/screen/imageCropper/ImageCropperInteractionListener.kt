package net.thechance.mena.identity.presentation.screen.imageCropper

import androidx.compose.ui.graphics.ImageBitmap
import net.thechance.mena.identity.presentation.base.BaseInteractionListener

interface ImageCropperInteractionListener : BaseInteractionListener {
    fun onCropImage(imageBitmap: ImageBitmap)
    fun onChangeImage(imageBitmap: ImageBitmap)
    fun onNavigateBack()
}