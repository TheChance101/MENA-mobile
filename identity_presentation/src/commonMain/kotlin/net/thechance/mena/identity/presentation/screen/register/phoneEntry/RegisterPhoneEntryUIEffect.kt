package net.thechance.mena.identity.presentation.screen.register.phoneEntry

sealed interface RegisterPhoneEntryUIEffect {
    data class NavigateToOTP(
        val phoneNumber: String,
        val callingCode: String,
        val countryCode: String
    ) : RegisterPhoneEntryUIEffect

    data object NavigateToLogin : RegisterPhoneEntryUIEffect
}