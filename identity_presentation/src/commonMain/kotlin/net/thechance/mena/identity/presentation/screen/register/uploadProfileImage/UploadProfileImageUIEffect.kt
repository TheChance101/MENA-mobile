package net.thechance.mena.identity.presentation.screen.register.uploadProfileImage

import net.thechance.mena.identity.domain.model.AuthenticationTokens

sealed interface UploadProfileImageUIEffect {
    data class NavigateToAccountCreated(val authTokens: AuthenticationTokens) : UploadProfileImageUIEffect
    data class NavigateToCropScreen(
        val imageKey: String,
        val onResult: (String) -> Unit
    ) : UploadProfileImageUIEffect
}