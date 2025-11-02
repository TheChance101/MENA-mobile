package net.thechance.mena.faith.presentation.feature.mosque.uploadImageScreen

import androidx.compose.ui.graphics.ImageBitmap

interface UploadImageInteractionListener {
    fun onImageCrop(image: ImageBitmap)
}