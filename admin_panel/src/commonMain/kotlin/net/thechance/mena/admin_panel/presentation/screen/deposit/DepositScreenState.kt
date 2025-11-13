package net.thechance.mena.admin_panel.presentation.screen.deposit

import net.thechance.mena.admin_panel.presentation.model.SnackBarState

data class DepositScreenState(
    val phoneNumber: String = "",
    val countryCode: String = "",
    val amount: Double = 0.0,
    val snackBar: SnackBarState = SnackBarState(),
    val isFillWalletButtonEnabled: Boolean = false
)