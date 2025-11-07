package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.dukanCart.DukanCartInteractionListener

object PreviewDukanCartInteractionListener : DukanCartInteractionListener {
    override fun onBackClicked() {}

    override fun onDukanClicked() {}

    override fun onCheckoutClicked() {}

    override fun onIncreaseItemQuantityClicked(productId: String, newQuantity: Int) {}

    override fun onDecreaseItemQuantityClicked(productId: String, newQuantity: Int) {}

    override fun onRemoveItemClicked(productId: String) {}

    override fun onRetryLoadCartClicked() {}

    override fun onDismissSnackBar() {}
}