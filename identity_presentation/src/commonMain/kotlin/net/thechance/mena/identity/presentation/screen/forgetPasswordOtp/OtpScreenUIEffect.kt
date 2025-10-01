package net.thechance.mena.identity.presentation.screen.forgetPasswordOtp

sealed class OtpScreenUIEffect {
    data class NavigateToResetPassword(
        val phoneNumber: String,
        val callingCode: String,
    ) : OtpScreenUIEffect()
    data object NavigateBack : OtpScreenUIEffect()
}