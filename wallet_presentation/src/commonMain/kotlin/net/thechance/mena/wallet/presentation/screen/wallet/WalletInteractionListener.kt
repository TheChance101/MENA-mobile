package net.thechance.mena.wallet.presentation.screen.wallet

interface WalletInteractionListener {
    fun onBackClicked()
    fun onRetryLoadBalanceClicked()
    fun onTransactionHistoryClicked()
}