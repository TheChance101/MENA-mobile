package net.thechance.mena.admin_panel.presentation.screen.deposit

interface DepositInteractionListener {
    fun onFillTheWalletButtonClicked()
    fun onPhoneNumberChanged(phoneNumber : String)
    fun onAmountChanged(amount : Double)
    fun onCountryCodeSelected()
    fun onCountryCodeChanged(country : DepositScreenState.CountryUiState)
    fun onCountryBottomSheetDismissed()
}