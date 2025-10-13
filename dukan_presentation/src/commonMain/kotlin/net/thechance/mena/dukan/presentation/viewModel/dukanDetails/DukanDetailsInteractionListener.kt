package net.thechance.mena.dukan.presentation.viewModel.dukanDetails


interface DukanDetailsInteractionListener {
    fun onBackClicked()
    fun onShelfClicked(id: String)
    fun onViewAllShelfProductsClicked(id: String, name: String)
    fun onViewDukanOnMapClicked(latitude: Double, longitude: Double)
    fun onCartClick(productId: String)
}