package net.thechance.mena.dukan.presentation.viewModel.approvedDukan

import net.thechance.mena.dukan.presentation.viewModel.createDukan.DukanCategoryUiState

interface ApprovedDukanInteractionListener {
    fun onBackButtonClicked()
    fun onDismissSnackBar()
    fun onCategorySelected(categoryId: String)
    fun onAddProductClicked()
    fun onEditShelfClicked()
    fun onAddShelfClicked()
    fun isShelfSelected(): (DukanCategoryUiState) -> Boolean
    fun onShelfSelected(shelf: DukanCategoryUiState): Boolean
    fun onShelfDeselected(shelf: DukanCategoryUiState): Boolean
    fun onShelfEnabled(shelf: DukanCategoryUiState): Boolean
}
