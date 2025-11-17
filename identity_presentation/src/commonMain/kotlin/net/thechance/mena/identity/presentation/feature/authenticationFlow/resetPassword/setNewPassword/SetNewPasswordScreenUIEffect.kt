package net.thechance.mena.identity.presentation.feature.authenticationFlow.resetPassword.setNewPassword

sealed interface SetNewPasswordScreenUIEffect {
    data object NavigateBackToLogin : SetNewPasswordScreenUIEffect
}