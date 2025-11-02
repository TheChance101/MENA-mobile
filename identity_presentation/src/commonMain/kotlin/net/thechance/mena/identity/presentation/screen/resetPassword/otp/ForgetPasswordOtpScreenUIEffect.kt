package net.thechance.mena.identity.presentation.screen.resetPassword.otp

sealed interface ForgetPasswordOtpScreenUIEffect {
    data object NavigateToResetPassword : ForgetPasswordOtpScreenUIEffect
    data object NavigateBack : ForgetPasswordOtpScreenUIEffect
}