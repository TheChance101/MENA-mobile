package net.thechance.mena.dukan.presentation.viewModel.manageShelf

import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState

data class ManageShelfUiState(
    val shelfTitle: String = "",
    val isSaveButtonEnabled: Boolean = false,
    val snackBarState: SnackBarUiState? = null,
    val showSnackBar: Boolean = false,
    val isLoading: Boolean = false,
)