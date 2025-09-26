package net.thechance.mena.dukan.presentation.viewModel.approvedDukan

import net.thechance.mena.dukan.presentation.viewModel.createDukan.DukanCategoryUiState

interface ApprovedDukanInteractionListener {
    fun onBackButtonClicked()
    fun onDismissSnackBar()
    fun onCategorySelected(categoryId: String)
    fun onAddProductClicked()
    fun onEditShelfClicked()
    
    // CategorySelectionRow methods
    fun isCategorySelected(): (DukanCategoryUiState) -> Boolean
    fun onCategorySelected(category: DukanCategoryUiState): Boolean
    fun onCategoryDeselected(category: DukanCategoryUiState): Boolean
    fun onCategoryEnabled(category: DukanCategoryUiState): Boolean
}
