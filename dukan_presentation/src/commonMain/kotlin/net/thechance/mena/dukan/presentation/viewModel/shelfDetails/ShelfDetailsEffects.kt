package net.thechance.mena.dukan.presentation.viewModel.shelfDetails

sealed class ShelfDetailsEffects {
    object NavigateBack : ShelfDetailsEffects()
    data class NavigateToCart(val dukanId: String) : ShelfDetailsEffects()
    data class NavigateToProductDetails(val productId: String,val dukanId: String) : ShelfDetailsEffects()
}