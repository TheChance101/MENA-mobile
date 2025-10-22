package net.thechance.mena.wallet.presentation.screen.statementsHistory

interface StatementsHistoryInteractionListener {
    fun onBackClicked()
    fun onRetryLoadStatementsHistoryClicked()
    fun onNextPageRequested()
    fun onStatementCardClicked(statement: StatementsHistoryScreenState.StatementItem)
    fun onEditClicked()
    fun onCancelEditModeClicked()
    fun onDeleteClicked(statement: StatementsHistoryScreenState.StatementItem)
}