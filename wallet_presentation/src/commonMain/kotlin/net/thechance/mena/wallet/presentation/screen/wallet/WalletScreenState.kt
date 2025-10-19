package net.thechance.mena.wallet.presentation.screen.wallet

import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.model.SnackBarState
import net.thechance.mena.wallet.presentation.base.UiState

data class WalletScreenState(
    val balance: Double = 0.0,
    val isLoading: Boolean = false,
    val errorState: ErrorState? = null,
    val snackBar: SnackBarState = SnackBarState()
)