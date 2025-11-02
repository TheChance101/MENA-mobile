package net.thechance.mena.identity.presentation.screen.forgetPasswordOtp

sealed interface OtpScreenUIEffect {
    data object NavigateToResetPassword : OtpScreenUIEffect
    data object NavigateBack : OtpScreenUIEffect
}