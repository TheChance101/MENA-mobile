package net.thechance.mena.wallet.presentation.screen.view_transactions_statement

sealed class ViewTransactionStatementEffect {
    data object NavigateBack: ViewTransactionStatementEffect()
    class ShareStatement(val statement: ByteArray): ViewTransactionStatementEffect()
}