package net.thechance.mena.wallet.presentation.screen.wallet

sealed interface WalletEffect {
    data object NavigateBack : WalletEffect
    data object NavigateToTransactionHistory : WalletEffect
    data object NavigateToStatementHistory : WalletEffect
}