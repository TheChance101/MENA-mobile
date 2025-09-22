package net.thechance.mena.wallet.presentation.screen.transactiondetails

sealed interface TransactionDetailsEffect {
    data object NavigateBack: TransactionDetailsEffect
}