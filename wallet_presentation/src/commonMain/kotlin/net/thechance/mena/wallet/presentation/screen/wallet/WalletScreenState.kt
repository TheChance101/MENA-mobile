package net.thechance.mena.wallet.presentation.screen.wallet

import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.model.SnackBarState

data class WalletScreenState(
    val balanceState: BalanceUiState = BalanceUiState(),
    val snackBar: SnackBarState = SnackBarState()
){
    data class BalanceUiState(
        val balance: Double = 0.0,
        val isLoading: Boolean = false,
        val errorState: ErrorState? = null,
    )
}