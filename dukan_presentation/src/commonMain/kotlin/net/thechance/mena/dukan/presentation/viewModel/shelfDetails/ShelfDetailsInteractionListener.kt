package net.thechance.mena.dukan.presentation.viewModel.shelfDetails

interface ShelfDetailsInteractionListener {
    fun onBackClicked()
    fun onAddToCartClicked(productId: String,productQuantity: Int)
    fun onPlusClicked(productId: String, productQuantity: Int)
    fun onMinusClicked(productId: String, productQuantity: Int)
    fun onCartClicked()
    fun onProductClicked(productId: String)
}