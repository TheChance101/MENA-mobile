package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ApprovedDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.DukanCategoryUiState

object PreviewApprovedDukanInteractionListener : ApprovedDukanInteractionListener {
    override fun onBackButtonClicked() {}
    override fun onDismissSnackBar() {}
    override fun onCategorySelected(categoryId: String) {}
    override fun onAddProductClicked() {}
    override fun onEditShelfClicked() {}
    
    // CategorySelectionRow methods
    override fun isCategorySelected(): (DukanCategoryUiState) -> Boolean = { false }
    override fun onCategorySelected(category: DukanCategoryUiState): Boolean = true
    override fun onCategoryDeselected(category: DukanCategoryUiState): Boolean = true
    override fun onCategoryEnabled(category: DukanCategoryUiState): Boolean = true
}
