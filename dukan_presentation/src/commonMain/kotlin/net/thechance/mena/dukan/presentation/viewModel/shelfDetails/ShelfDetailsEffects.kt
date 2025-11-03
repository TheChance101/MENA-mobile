package net.thechance.mena.dukan.presentation.viewModel.shelfDetails

sealed class ShelfDetailsEffects {
    object NavigateBack : ShelfDetailsEffects()
    data class NavigateToCart(val dukanId: String) : ShelfDetailsEffects()
}