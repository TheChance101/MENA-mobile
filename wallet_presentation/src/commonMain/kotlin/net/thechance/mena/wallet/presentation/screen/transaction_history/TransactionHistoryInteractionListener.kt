package net.thechance.mena.wallet.presentation.screen.transaction_history

interface TransactionHistoryInteractionListener {
    fun onBackClicked()
    fun onTransactionCardClicked()
    fun onShareClicked()
    fun onFilterClicked()

}