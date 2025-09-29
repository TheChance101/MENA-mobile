package net.thechance.mena.identity.presentation.screen.forgetPasswordOTP

sealed class OTPScreenUIEffect {
    data class NavigateToResetPassword(
        val phoneNumber: String,
        val callingCode: String,
    ) : OTPScreenUIEffect()
    data object NavigateBack : OTPScreenUIEffect()
}