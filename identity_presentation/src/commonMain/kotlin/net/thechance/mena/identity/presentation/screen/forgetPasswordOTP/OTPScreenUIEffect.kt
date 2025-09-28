package net.thechance.mena.identity.presentation.screen.forgetPasswordOTP

sealed class OTPScreenUIEffect {
    data object NavigateToResetPassword : OTPScreenUIEffect()
    data object NavigateBack : OTPScreenUIEffect()
}