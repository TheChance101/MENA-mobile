package net.thechance.mena.admin_panel.presentation.screen.deposit

import net.thechance.mena.admin_panel.presentation.base.ErrorState
import net.thechance.mena.admin_panel.presentation.model.SnackBarState

data class DepositScreenState(
    val phoneNumber: String = "",
    val selectedCountry: CountryUiState = CountryUiState(),
    val amount: String = "",
    val snackBar: SnackBarState = SnackBarState(),
    val isCountriesLoading: Boolean = false,
    val isDepositProcessLoading :Boolean = false,
    val availableCountries : List<CountryUiState> =emptyList(),
    val errorState: ErrorState? = null
) {
    val isFillWalletButtonEnabled: Boolean
        get() {
            return phoneNumber.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0
        }

    data class CountryUiState(
        val name: String = "",
        val callingCode: String = "",
        val countryCodeName: String = "",
        val flagEmoji: String = "",
        val phoneNumberRegex: String = "",
    )
}
interface DepositErrorState : ErrorState {
    data object NoAccount : ErrorState
}