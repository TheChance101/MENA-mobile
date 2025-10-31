package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.dukanCart.DukanCartInteractionListener

object PreviewDukanCartInteractionListener : DukanCartInteractionListener {
    override fun onBackClick() {}

    override fun onDukanDetailsClick() {}

    override fun onCheckoutClick() {}

    override fun onIncreaseItemQuantityClick(cartItemId: String) {}

    override fun onDecreaseItemQuantityClick(cartItemId: String) {}

    override fun onRemoveItemClick(cartItemId: String) {}

    override fun onRetryLoadCartClick() {}
}