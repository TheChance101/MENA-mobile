package net.thechance.mena.wallet.presentation.screen.export

import net.thechance.mena.wallet.presentation.utils.StorageLocation

interface ExportTransactionsEffect {
    data object NavigateBack : ExportTransactionsEffect
    data class NavigateToViewFileScreen(val statementLocation: StorageLocation) : ExportTransactionsEffect
}