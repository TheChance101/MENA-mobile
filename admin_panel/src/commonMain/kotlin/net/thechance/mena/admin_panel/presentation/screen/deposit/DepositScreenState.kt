package net.thechance.mena.admin_panel.presentation.screen.deposit

import net.thechance.mena.admin_panel.presentation.base.ErrorState
import net.thechance.mena.admin_panel.presentation.model.SnackBarState

data class DepositScreenState(
    val phoneNumber: String = "",
    val country: CountryUiState = CountryUiState(),
    val amount: String = "",
    val snackBar: SnackBarState = SnackBarState(),
    val isLoadingCountries: Boolean = false,
    val isDepositProcessLoading :Boolean = false,
    val availableCountries : List<CountryUiState> =emptyList()
) {
    val isFillWalletButtonEnabled: Boolean
        get() {
            val cleanedAmount = amount.replace(",", "")
            return phoneNumber.isNotBlank() && (cleanedAmount.toDoubleOrNull() ?: 0.0) > 0
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