package net.thechance.mena.faith.presentation.feature.mosque.uploadImageScreen

import androidx.compose.ui.graphics.ImageBitmap
import com.attafitamim.krop.core.images.ImageSrc

data class UploadImageUiState(
    val imageSrc: ImageSrc? = null,
    val croppedImage: ImageBitmap? = null,
)