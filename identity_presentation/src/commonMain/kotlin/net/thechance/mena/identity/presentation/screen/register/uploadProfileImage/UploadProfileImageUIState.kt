package net.thechance.mena.identity.presentation.screen.register.uploadProfileImage

import androidx.compose.ui.graphics.ImageBitmap
import org.jetbrains.compose.resources.StringResource

data class UploadProfileImageUIState(
    val imageBitmap: ImageBitmap? = null,
    val isLoading: Boolean = false,
    val isUploadEnabled: Boolean = false,
    val isImageLoaded: Boolean = false,
    val errorMessage: StringResource? = null,
)