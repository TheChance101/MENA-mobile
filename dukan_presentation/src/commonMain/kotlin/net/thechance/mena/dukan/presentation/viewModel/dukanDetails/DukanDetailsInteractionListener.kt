package net.thechance.mena.dukan.presentation.viewModel.dukanDetails


interface DukanDetailsInteractionListener {
    fun onBackClicked()
    fun onShelfClicked(id: String)
    fun onViewAllProductsShelfClicked(id: String, name: String)
    fun onViewDukanOnMapClicked(latitude: Double, longitude: Double)
    fun onAddToCartClicked(productId: String,productQuantity: Int)
    fun onPlusClicked(productId: String, productQuantity: Int)
    fun onMinusClicked(productId: String, productQuantity: Int)
    fun onDismissSnackBar()
    fun onViewCartClicked()
    fun onRetryClicked()
    fun onProductClicked(productId: String)
    fun onFavoriteDukanClicked(dukanId: String)
}