package net.thechance.mena.identity.presentation.feature.profileFlow.imageCropper

sealed interface ImageCropperScreenEffect {
    object NavigateBackToEditProfile : ImageCropperScreenEffect
    class NavigateBackToEditProfileWithImage(val imageKey: String) : ImageCropperScreenEffect
}