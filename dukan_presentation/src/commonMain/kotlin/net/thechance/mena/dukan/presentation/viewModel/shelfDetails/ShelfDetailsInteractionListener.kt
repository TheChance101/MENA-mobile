package net.thechance.mena.dukan.presentation.viewModel.shelfDetails

interface ShelfDetailsInteractionListener {
    fun onBackClicked()
    fun onAddToCartClicked(productId: String)
    fun onProductClicked(productId: String)
}