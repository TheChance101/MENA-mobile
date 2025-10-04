package net.thechance.mena.identity.presentation.screen.login

import net.thechance.mena.identity.presentation.bottomSheet.countryPicker.menaCountries.MenaCountry

data class LoginScreenUIState(
    val phoneNumber: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val showCountryBottomSheet: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginEnabled: Boolean = false,
    val currentCountry: MenaCountry = MenaCountry.IRAQ,
)