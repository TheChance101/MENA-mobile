package net.thechance.mena.identity.presentation.screen.uploadProfileImage

sealed interface UploadProfileImageUIEffect {
    data object NavigateToNextScreen : UploadProfileImageUIEffect
    data object NavigateToNextScreenAfterSkip : UploadProfileImageUIEffect
}