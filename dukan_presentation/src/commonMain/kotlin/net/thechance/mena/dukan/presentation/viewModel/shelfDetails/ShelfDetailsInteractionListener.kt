package net.thechance.mena.dukan.presentation.viewModel.shelfDetails

interface ShelfDetailsInteractionListener {
    fun onBackClicked()
    fun onAddToCartClicked(productId: String)
    fun onPlusClicked(productId: String)
    fun onMinusClicked(productId: String)
    fun onCartClicked()
}