package net.thechance.mena.dukan.presentation.viewModel.productDetails


interface ProductDetailsInteractionListener {
    fun onBackClicked()
    fun onAddToCartClicked(productId: String )
    fun onPlusClicked(productId: String)
    fun onMinusClicked(productId: String)
    fun onDismissSnackBar()
    fun onViewCartClicked()
    fun onShareClicked()
    fun onToggleProductToFavoriteClicked()
    fun onSecondaryImageClicked(imageUrl: String)
    fun onRetryClicked()
}