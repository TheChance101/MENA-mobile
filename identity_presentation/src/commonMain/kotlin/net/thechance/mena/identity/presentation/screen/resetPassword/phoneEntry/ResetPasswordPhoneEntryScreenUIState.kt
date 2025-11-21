package net.thechance.mena.identity.presentation.screen.resetPassword.phoneEntry

import net.thechance.mena.identity.presentation.screen.countryPicker.menaCountries.MenaCountry

data class ResetPasswordPhoneEntryScreenUIState(
    val phoneNumber: String = "",
    val showCountryBottomSheet: Boolean = false,
    val currentCountry: MenaCountry = MenaCountry.IRAQ,
    val isContinueEnabled: Boolean = false,
    val isLoading: Boolean = false,
)