package net.thechance.mena.identity.presentation.screen.editProfile

import androidx.compose.ui.graphics.ImageBitmap

sealed class EditUserProfileUIEffect {
    object NavigateBackToProfile : EditUserProfileUIEffect()
    data class NavigateToCropScreen(
        val imageBitmap: ImageBitmap,
        val onResult: (ImageBitmap) -> Unit
    ) : EditUserProfileUIEffect()
}