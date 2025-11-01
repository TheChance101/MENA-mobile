package net.thechance.mena.identity.presentation.screen.imageCropper

import androidx.compose.ui.graphics.ImageBitmap

sealed interface ImageCropperScreenEffect {
    object NavigateBackToEditProfile : ImageCropperScreenEffect
    class NavigateBackToEditProfileWithImage(val imageKey: String) : ImageCropperScreenEffect
}