package net.thechance.mena.faith.presentation.feature.mosque.uploadImageScreen

import androidx.compose.ui.graphics.ImageBitmap

sealed interface UploadImageEffect {
    data class NavigateBack(val image: ImageBitmap) : UploadImageEffect
}