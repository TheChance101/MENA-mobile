package net.thechance.mena.identity.presentation.screen.forgetPassword

import net.thechance.mena.identity.presentation.bottomSheet.countryPicker.CountryPickerUIState

data class ForgetPasswordScreenUIState(
    val phoneNumber: String = "",
    val showCountryBottomSheet: Boolean = false,
    val countryPickerUIState: CountryPickerUIState = CountryPickerUIState(),
    val isContinueEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)