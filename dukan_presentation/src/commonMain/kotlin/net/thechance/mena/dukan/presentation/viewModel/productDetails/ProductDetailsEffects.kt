package net.thechance.mena.dukan.presentation.viewModel.productDetails

sealed interface ProductDetailsEffects {
    object NavigateBack : ProductDetailsEffects
    class NavigateToCart (dukanId: String): ProductDetailsEffects
    object NavigateToFavorites : ProductDetailsEffects
}