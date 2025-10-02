package net.thechance.mena.identity.presentation.screen.resetPassword

sealed interface ResetPasswordScreenUIEffect {
    data object NavigateBackToLogin : ResetPasswordScreenUIEffect
}