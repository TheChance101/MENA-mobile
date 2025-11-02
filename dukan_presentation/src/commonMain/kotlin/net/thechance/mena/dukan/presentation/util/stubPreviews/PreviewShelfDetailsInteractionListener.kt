package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.shelfDetails.ShelfDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.shelfDetails.ShelfDetailsUiState

object PreviewShelfDetailsInteractionListener : ShelfDetailsInteractionListener {
    override fun onBackClicked() {}
    override fun onAddToCartClicked(productId: String, toggleCartToQuantity: Boolean) {}
    override fun onPlusClicked(productId: String, productQuantity: Int) {}
    override fun onMinusClicked(productId: String, productQuantity: Int) {}
    override fun onCartClicked() {}
}