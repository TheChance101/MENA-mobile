package net.thechance.mena.dukan.presentation.viewModel.productDetails

sealed interface ProductDetailsEffects {
    object NavigateBack : ProductDetailsEffects
    class NavigateToCart (val dukanId: String , val productId: String): ProductDetailsEffects
    object NavigateToFavorites : ProductDetailsEffects
}