package net.thechance.mena.identity.presentation.screen.uploadProfileImage

sealed interface UploadProfileImageUIEffect {
    data object NavigateToNextScreen : UploadProfileImageUIEffect
    data object NavigateToNextScreenAfterSkip : UploadProfileImageUIEffect
    data class NavigateToCropScreen(
        val imageKey: String,
        val onResult: (String) -> Unit
    ) : UploadProfileImageUIEffect}