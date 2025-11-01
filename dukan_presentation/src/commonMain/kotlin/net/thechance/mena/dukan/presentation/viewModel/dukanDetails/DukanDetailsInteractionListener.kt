package net.thechance.mena.dukan.presentation.viewModel.dukanDetails


interface DukanDetailsInteractionListener {
    fun onBackClicked()
    fun onShelfClicked(id: String)
    fun onViewAllProductsShelfClicked(id: String, name: String)
    fun onViewDukanOnMapClicked(latitude: Double, longitude: Double)
    fun onAddToCartClicked(product: DukanDetailsUiState.ProductUiState)
    fun onPlusClicked(product: DukanDetailsUiState.ProductUiState)
    fun onMinusClicked(product: DukanDetailsUiState.ProductUiState)
    fun onCartClicked()
    fun onRetryClicked()
}