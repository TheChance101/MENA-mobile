package net.thechance.mena.dukan.presentation.viewModel.productDetails


interface ProductDetailsInteractionListener {
    fun onBackClicked()
    fun onAddToCartClick(productId: String)
    fun onShareButtonClicked()
    fun onAddToFavoritesButtonClicked()
    fun onViewCartButtonClicked()
    fun onSecondaryImageClicked(imageUrl: String)
}