package net.thechance.mena.faith.presentation.feature.mosque.mosqueImageCrop

import androidx.compose.ui.graphics.ImageBitmap

sealed interface MosqueImageCropEffect {
    data class NavigateBack(val image: ImageBitmap) : MosqueImageCropEffect
}