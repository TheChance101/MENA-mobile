package net.thechance.mena.identity.presentation.screen.register.uploadProfileImage

import net.thechance.mena.identity.presentation.screen.register.shared.AuthUIState
import org.jetbrains.compose.resources.StringResource

sealed interface UploadProfileImageUIEffect {
    data class NavigateToAccountCreated(val authUiState: AuthUIState) : UploadProfileImageUIEffect

    data class NavigateToCropScreen(
        val imageKey: String,
        val onResult: (String) -> Unit
    ) : UploadProfileImageUIEffect

    data class ShowSnackBarError(val errorStringResource: StringResource) :
        UploadProfileImageUIEffect
}