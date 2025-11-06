package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.shelfDetails.ShelfDetailsInteractionListener

object PreviewShelfDetailsInteractionListener : ShelfDetailsInteractionListener {
    override fun onBackClicked() {}
    override fun onAddToCartClicked(productId: String,productQuantity: Int) {}
    override fun onPlusClicked(productId: String, productQuantity: Int) {}
    override fun onMinusClicked(productId: String, productQuantity: Int) {}
    override fun onViewCartClicked() {}
    override fun onProductClicked(productId: String) {}
    override fun onDismissSnackBar() {}
}