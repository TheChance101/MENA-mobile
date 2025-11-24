package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.checkout.CheckoutInteractionListener

object PreviewCheckoutInteractionListener : CheckoutInteractionListener {
    override fun onBackClicked() {}
    override fun onConfirmOrderClicked() {}
    override fun onChangeLocationClicked() {}
    override fun onDismissSnackBar() {}
}