package net.thechance.mena.wallet.presentation.screen.statement_details

sealed interface StatementDetailsEffect {
    data object NavigateBack: StatementDetailsEffect
    class ShareStatement(val statement: ByteArray): StatementDetailsEffect
}