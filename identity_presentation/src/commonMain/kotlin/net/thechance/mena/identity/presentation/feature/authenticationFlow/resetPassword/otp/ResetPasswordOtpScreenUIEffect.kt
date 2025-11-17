package net.thechance.mena.identity.presentation.feature.authenticationFlow.resetPassword.otp

sealed interface ResetPasswordOtpScreenUIEffect {
    data object NavigateToResetPassword : ResetPasswordOtpScreenUIEffect
    data object NavigateBack : ResetPasswordOtpScreenUIEffect
}