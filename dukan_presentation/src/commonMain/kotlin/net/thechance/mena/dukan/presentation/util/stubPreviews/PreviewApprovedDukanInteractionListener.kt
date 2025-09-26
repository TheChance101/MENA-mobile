package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ApprovedDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.DukanCategoryUiState

object PreviewApprovedDukanInteractionListener : ApprovedDukanInteractionListener {
    override fun onBackButtonClicked() {}
    override fun onDismissSnackBar() {}
    override fun onCategorySelected(categoryId: String) {}
    override fun onAddProductClicked() {}
    override fun onEditShelfClicked() {}
    override fun onAddShelfClicked() {}
    override fun isShelfSelected(): (DukanCategoryUiState) -> Boolean = { false }
    override fun onShelfSelected(shelf: DukanCategoryUiState): Boolean = true
    override fun onShelfDeselected(shelf: DukanCategoryUiState): Boolean = true
    override fun onShelfEnabled(shelf: DukanCategoryUiState): Boolean = true
}
