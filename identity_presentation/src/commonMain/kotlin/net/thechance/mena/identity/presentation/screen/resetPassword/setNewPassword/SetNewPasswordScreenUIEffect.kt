package net.thechance.mena.identity.presentation.screen.resetPassword.setNewPassword

import org.jetbrains.compose.resources.StringResource

sealed interface SetNewPasswordScreenUIEffect {
    data object NavigateBackToLogin : SetNewPasswordScreenUIEffect
    data class ShowSnackBarError(val errorStringResource: StringResource) :
        SetNewPasswordScreenUIEffect

}