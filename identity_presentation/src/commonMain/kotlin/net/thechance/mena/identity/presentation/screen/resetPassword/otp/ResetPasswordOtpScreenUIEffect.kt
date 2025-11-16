package net.thechance.mena.identity.presentation.screen.resetPassword.otp

sealed interface ResetPasswordOtpScreenUIEffect {
    data object NavigateToResetPassword : ResetPasswordOtpScreenUIEffect
    data object NavigateBack : ResetPasswordOtpScreenUIEffect
}