package net.thechance.mena.admin_panel.presentation.screen.deposit

interface DepositInteractionListener {
    fun onFillTheWalletButtonClicked()
    fun onPhoneNumberChanged(phoneNumber : String)
    fun onAmountChanged(amount :String)
    fun onRetryClicked()
    fun onCountryCodeChanged(country : DepositScreenState.CountryUiState)
}