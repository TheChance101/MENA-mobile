package net.thechance.mena.identity.presentation.screen.register.phoneEntry

import net.thechance.mena.identity.presentation.screen.countryPicker.menaCountries.MenaCountry

data class RegisterPhoneEntryUIState(
    val phoneNumber: String = "",
    val showCountryBottomSheet: Boolean = false,
    val currentCountry: MenaCountry = MenaCountry.IRAQ,
    val isRegisterEnabled: Boolean = false,
    val isLoading: Boolean = false,
)