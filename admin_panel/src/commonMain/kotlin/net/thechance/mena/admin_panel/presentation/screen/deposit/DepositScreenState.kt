package net.thechance.mena.admin_panel.presentation.screen.deposit

import net.thechance.mena.admin_panel.presentation.model.SnackBarState

data class DepositScreenState(
    val phoneNumber: String = "",
    val country: CountryUiState = CountryUiState(),
    val amount: Double = 0.0,
    val snackBar: SnackBarState = SnackBarState(),
    val isFillWalletButtonEnabled: Boolean = false,
    val isCountryBottomSheetVisible: Boolean = false,
) {
    data class CountryUiState(
        val name: String = "",
        val callingCode: String = "",
        val countryCodeName: String = "",
        val flagEmoji: String = "",
        val phoneNumberRegex: String = "",
    )
}