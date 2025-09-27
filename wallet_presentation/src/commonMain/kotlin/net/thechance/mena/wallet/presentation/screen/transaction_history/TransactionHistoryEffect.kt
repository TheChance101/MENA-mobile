package net.thechance.mena.wallet.presentation.screen.transaction_history

sealed class TransactionHistoryEffect {
    data object NavigateBack : TransactionHistoryEffect()
}