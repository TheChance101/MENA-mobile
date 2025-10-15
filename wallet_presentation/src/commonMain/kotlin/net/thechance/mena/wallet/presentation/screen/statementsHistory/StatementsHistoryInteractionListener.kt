package net.thechance.mena.wallet.presentation.screen.statementsHistory

interface StatementsHistoryInteractionListener {
    fun onBackClicked()
    fun onRetryLoadStatementsHistoryClicked()
    fun onNextPageRequested()
    fun onStatementCardClicked(id: Long)
    fun onEditClicked()
    fun onCancelEditModeClicked()
    fun onDeleteClicked(id: Long)
}