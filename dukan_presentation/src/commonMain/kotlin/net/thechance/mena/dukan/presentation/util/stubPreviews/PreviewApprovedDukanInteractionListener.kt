package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ApprovedDukanInteractionListener

object PreviewApprovedDukanInteractionListener : ApprovedDukanInteractionListener {
    override fun onBackButtonClicked() {}
    override fun onDismissSnackBar() {}
    override fun onAddProductClicked() {}
    override fun onEditShelfClicked() {}
    override fun onAddShelfClicked() {}
    override fun onProductClick(product: Product) {}
    override fun onShelfAddedSuccessfully() {}
    override fun isShelfSelected(): (Shelf) -> Boolean = { false }
    override fun onShelfSelected(shelf: Shelf): Boolean = true
    override fun onShelfDeselected(shelf: Shelf): Boolean = true
    override fun onShelfEnabled(shelf: Shelf): Boolean = true
}
