package net.thechance.mena.wallet.presentation.screen.view_transactions

sealed class ViewTransactionsEffect {
    data object NavigateBack : ViewTransactionsEffect()
}