package net.thechance.mena.dukan.presentation.viewModel.createShelf

import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState

data class CreateShelfUiState(
    val shelfTitle: String = "",
    val isCreateButtonEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val snackBarState: SnackBarUiState? = null,
)