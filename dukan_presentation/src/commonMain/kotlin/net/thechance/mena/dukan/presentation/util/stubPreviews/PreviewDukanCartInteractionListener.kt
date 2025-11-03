package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.dukanCart.DukanCartInteractionListener

object PreviewDukanCartInteractionListener : DukanCartInteractionListener {
    override fun onBackClicked() {}

    override fun onDukanClicked() {}

    override fun onCheckoutClicked() {}

    override fun onIncreaseItemQuantityClicked(cartItemId: String) {}

    override fun onDecreaseItemQuantityClicked(cartItemId: String) {}

    override fun onRemoveItemClicked(cartItemId: String) {}

    override fun onRetryLoadCartClicked() {}
}