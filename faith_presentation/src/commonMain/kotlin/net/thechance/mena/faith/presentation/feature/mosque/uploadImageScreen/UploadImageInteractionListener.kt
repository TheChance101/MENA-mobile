package net.thechance.mena.faith.presentation.feature.mosque.uploadImageScreen

import androidx.compose.ui.graphics.ImageBitmap

internal interface UploadImageInteractionListener {
    fun onImageCrop(image: ImageBitmap)
    fun onBackClick()
}