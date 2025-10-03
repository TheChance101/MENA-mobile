package net.thechance.mena.identity.presentation.screen.forgetPasswordOtp

sealed class OtpScreenUIEffect {
    data object NavigateToResetPassword : OtpScreenUIEffect()
    data object NavigateBack : OtpScreenUIEffect()
}