package net.thechance.mena.identity.presentation.screen.forget_password

sealed class ForgetPasswordScreenUIEffect {
    data class NavigateToOTP(
        val phoneNumber: String,
        val countryCode: String
    ) : ForgetPasswordScreenUIEffect()

    data object NavigateBack : ForgetPasswordScreenUIEffect()
}