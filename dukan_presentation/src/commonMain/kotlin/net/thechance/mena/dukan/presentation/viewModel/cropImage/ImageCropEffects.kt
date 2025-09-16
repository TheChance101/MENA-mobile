package net.thechance.mena.dukan.presentation.viewModel.cropImage

import androidx.compose.ui.graphics.ImageBitmap

sealed class ImageCropEffects {
    data class NavigateBack(
        val image: ImageBitmap
    ) : ImageCropEffects()
}