package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener

object PreviewDukanDetailsInteractionListener : DukanDetailsInteractionListener {
    override fun onBackClicked() {}
    override fun onShelfClicked(id: String) {}
    override fun onViewAllProductsShelfClicked(id: String, name: String) {}
    override fun onViewDukanOnMapClicked(latitude: Double, longitude: Double) {}
    override fun onAddToCartClicked(productId: String,productQuantity: Int) {}
    override fun onPlusClicked(productId: String, productQuantity: Int) {}
    override fun onMinusClicked(productId: String, productQuantity: Int) {}
    override fun onDismissSnackBar() {}
    override fun onViewCartClicked() {}
    override fun onRetryClicked() {}
    override fun onProductClicked(productId: String) {}
    override fun onFavoriteDukanClicked(dukanId: String) {}
}