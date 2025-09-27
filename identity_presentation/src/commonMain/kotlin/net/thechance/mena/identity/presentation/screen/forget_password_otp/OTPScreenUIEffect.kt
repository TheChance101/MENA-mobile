package net.thechance.mena.identity.presentation.screen.forget_password_otp

sealed class OTPScreenUIEffect {
    data object NavigateToResetPassword : OTPScreenUIEffect()
    data object NavigateBack : OTPScreenUIEffect()
}