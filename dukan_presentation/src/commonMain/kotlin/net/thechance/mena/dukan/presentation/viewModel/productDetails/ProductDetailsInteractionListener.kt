package net.thechance.mena.dukan.presentation.viewModel.productDetails


interface ProductDetailsInteractionListener {
    fun onBackClicked()
    fun onAddToCartClicked(productId: String)
    fun onShareClicked()
    fun onAddToFavoritesClicked()
    fun onViewCartClicked()
    fun onSecondaryImageClicked(imageUrl: String)
    fun onRetryClicked()
}