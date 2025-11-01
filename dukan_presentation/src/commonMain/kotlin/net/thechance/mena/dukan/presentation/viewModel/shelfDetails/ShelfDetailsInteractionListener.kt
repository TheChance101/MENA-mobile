package net.thechance.mena.dukan.presentation.viewModel.shelfDetails

interface ShelfDetailsInteractionListener {
    fun onBackClicked()
    fun onAddToCartClicked(product: ShelfDetailsUiState.ProductUiState)
    fun onPlusClicked(product: ShelfDetailsUiState.ProductUiState)
    fun onMinusClicked(product: ShelfDetailsUiState.ProductUiState)
    fun onCartClicked()
}