package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.shelfDetails.ShelfDetailsInteractionListener

object PreviewShelfDetailsInteractionListener : ShelfDetailsInteractionListener {
    override fun onBackClicked() {}
    override fun onAddToCartClick(productId: String) {}
}