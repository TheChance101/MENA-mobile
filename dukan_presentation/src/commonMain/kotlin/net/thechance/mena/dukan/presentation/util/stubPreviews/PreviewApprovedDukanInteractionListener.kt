package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ApprovedDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ShelfUiState

object PreviewApprovedDukanInteractionListener : ApprovedDukanInteractionListener {
    override fun onBackButtonClicked() {}
    override fun onDismissSnackBar() {}
    override fun onAddProductClicked() {}
    override fun onEditShelfClicked() {}
    override fun onAddShelfClicked() {}
    override fun isShelfSelected(): (ShelfUiState) -> Boolean = { false }
    override fun onShelfSelected(shelf: ShelfUiState): Boolean = true
    override fun onShelfDeselected(shelf: ShelfUiState): Boolean = true
    override fun onShelfEnabled(shelf: ShelfUiState): Boolean = true
}
