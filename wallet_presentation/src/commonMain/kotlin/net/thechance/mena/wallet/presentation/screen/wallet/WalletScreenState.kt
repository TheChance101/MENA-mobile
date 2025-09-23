package net.thechance.mena.wallet.presentation.screen.wallet

import net.thechance.mena.wallet.presentation.base.SnackBarState
import net.thechance.mena.wallet.presentation.base.UiState

data class WalletScreenState(
    val balance: UiState<Double> = UiState.Idle,
    val snackBar: SnackBarState = SnackBarState()
)
