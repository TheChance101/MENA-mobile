package net.thechance.mena.identity.presentation.screen.resetPassword.setNewPassword

sealed interface SetNewPasswordScreenUIEffect {
    data object NavigateBackToLogin : SetNewPasswordScreenUIEffect
}