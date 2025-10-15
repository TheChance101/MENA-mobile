package net.thechance.mena.wallet.presentation.screen.statement_details

sealed class StatementDetailsEffect {
    data object NavigateBack: StatementDetailsEffect()
    class ShareStatement(val statement: ByteArray): StatementDetailsEffect()
}