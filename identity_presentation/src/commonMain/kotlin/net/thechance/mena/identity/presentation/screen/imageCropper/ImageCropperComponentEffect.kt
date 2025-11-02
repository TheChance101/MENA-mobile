package net.thechance.mena.identity.presentation.screen.imageCropper

import androidx.compose.ui.graphics.ImageBitmap

sealed interface ImageCropperComponentEffect {
    data class SaveImage(val imageBitmap: ImageBitmap) : ImageCropperComponentEffect
    data class UploadAnotherImage(val imageBitmap: ImageBitmap) : ImageCropperComponentEffect
}