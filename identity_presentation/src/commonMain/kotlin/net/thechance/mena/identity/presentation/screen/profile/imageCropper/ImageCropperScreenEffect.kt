package net.thechance.mena.identity.presentation.screen.profile.imageCropper

import org.jetbrains.compose.resources.StringResource


sealed interface ImageCropperScreenEffect {
    object NavigateBackToEditProfile : ImageCropperScreenEffect
    class NavigateBackToEditProfileWithImage(val imageKey: String) : ImageCropperScreenEffect

    data class ShowSnackBarError(val errorStringResource: StringResource) : ImageCropperScreenEffect
}