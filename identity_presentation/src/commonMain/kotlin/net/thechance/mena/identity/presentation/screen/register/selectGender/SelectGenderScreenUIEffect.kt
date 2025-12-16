package net.thechance.mena.identity.presentation.screen.register.selectGender

import net.thechance.mena.identity.presentation.screen.register.shared.AuthUiState
import org.jetbrains.compose.resources.StringResource

sealed interface SelectGenderScreenUIEffect {
    data class NavigateToUploadProfileImage(val authUiState: AuthUiState) : SelectGenderScreenUIEffect

    data class ShowSnackBarError(val errorStringResource: StringResource) :
        SelectGenderScreenUIEffect

    data object NavigateBackToRegister: SelectGenderScreenUIEffect
}