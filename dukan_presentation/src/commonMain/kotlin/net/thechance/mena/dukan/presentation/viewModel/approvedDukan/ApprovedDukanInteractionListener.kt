package net.thechance.mena.dukan.presentation.viewModel.approvedDukan

interface ApprovedDukanInteractionListener {
    fun onBackButtonClicked()
    fun onDismissSnackBar()
    fun onAddProductClicked()
    fun onEditShelfClicked()
    fun onAddShelfClicked()
    fun isShelfSelected(): (ShelfUiState) -> Boolean
    fun onShelfSelected(shelf: ShelfUiState): Boolean
    fun onShelfDeselected(shelf: ShelfUiState): Boolean
    fun onShelfEnabled(shelf: ShelfUiState): Boolean
}
