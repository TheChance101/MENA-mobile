package net.thechance.mena.identity.presentation.screen.uploadProfileImage

import androidx.compose.ui.graphics.ImageBitmap

data class UploadProfileImageUIState(
    val imageBitmap: ImageBitmap? = null,
    val isLoading: Boolean = false,
    val errorMessage: Int? = null,
    val isUploadEnabled: Boolean = false,
    val isImageLoaded: Boolean = false,
)