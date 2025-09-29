package net.thechance.mena.identity.presentation.screen.forgetPassword

sealed class ForgetPasswordScreenUIEffect {
    data class NavigateToOTP(
        val phoneNumber: String,
        val callingCode: String,
        val countryCode: String
    ) : ForgetPasswordScreenUIEffect()

    data object NavigateBack : ForgetPasswordScreenUIEffect()
}