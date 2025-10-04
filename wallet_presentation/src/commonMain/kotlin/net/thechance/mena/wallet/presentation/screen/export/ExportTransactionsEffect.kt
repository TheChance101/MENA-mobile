package net.thechance.mena.wallet.presentation.screen.export

import net.thechance.mena.wallet.domain.model.TransactionFilterParams

interface ExportTransactionsEffect {
    data object NavigateBack : ExportTransactionsEffect
    data class NavigateToViewFileScreen(val filterParams: TransactionFilterParams) : ExportTransactionsEffect
}