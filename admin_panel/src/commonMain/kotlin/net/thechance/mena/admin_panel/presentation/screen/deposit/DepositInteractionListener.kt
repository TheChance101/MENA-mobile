package net.thechance.mena.admin_panel.presentation.screen.deposit

interface DepositInteractionListener {
    fun onFillTheWalletButtonClicked()
    fun onPhoneNumberChanged()
    fun onAmountChanged()
    fun onCountryCodeSelected()
}