package net.thechance.mena.wallet.presentation.screen.transaction_details

sealed interface TransactionDetailsEffect {
    data object NavigateBack: TransactionDetailsEffect
}