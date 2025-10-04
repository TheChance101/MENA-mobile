package net.thechance.mena.identity.presentation.screen.forgetPassword

import net.thechance.mena.identity.presentation.bottomSheet.countryPicker.menaCountries.MenaCountry

data class ForgetPasswordScreenUIState(
    val phoneNumber: String = "",
    val showCountryBottomSheet: Boolean = false,
    val currentCountry: MenaCountry = MenaCountry.IRAQ,
    val isContinueEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)