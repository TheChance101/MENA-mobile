package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.productDetails.ProductDetailsInteractionListener

object PreviewProductDetailsInteractionListener : ProductDetailsInteractionListener {
    override fun onBackClicked() {
    }

    override fun onAddToCartClicked(productId: String) {
    }

    override fun onPlusClicked(productId: String) {

    }

    override fun onMinusClicked(productId: String) {
    }

    override fun onDismissSnackBar() {
    }

    override fun onToggleProductToFavoriteClicked() {
    }

    override fun onViewCartClicked() {
    }

    override fun onSecondaryImageClicked(imageUrl: String, selectedImageUrl: String) {
    }

    override fun onRetryClicked() {
    }
}