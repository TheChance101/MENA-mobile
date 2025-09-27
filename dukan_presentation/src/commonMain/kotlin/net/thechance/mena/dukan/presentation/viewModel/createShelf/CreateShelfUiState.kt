package net.thechance.mena.dukan.presentation.viewModel.createShelf

import net.thechance.mena.dukan.presentation.component.SnackBarMessage

data class CreateShelfUiState(
    val shelfTitle: String = "",
    val isCreateButtonEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val snackBarMessage: SnackBarMessage? = null,
    val showShelfAddedSuccess: Boolean = false,
)