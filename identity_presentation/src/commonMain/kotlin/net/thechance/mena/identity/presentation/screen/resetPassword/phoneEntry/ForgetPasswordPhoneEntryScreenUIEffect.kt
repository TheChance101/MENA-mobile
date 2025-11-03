package net.thechance.mena.identity.presentation.screen.resetPassword.phoneEntry

sealed interface ForgetPasswordPhoneEntryScreenUIEffect {
    data class NavigateToOTP(
        val phoneNumber: String,
        val callingCode: String,
        val countryCode: String
    ) : ForgetPasswordPhoneEntryScreenUIEffect

    data object NavigateBack : ForgetPasswordPhoneEntryScreenUIEffect
}