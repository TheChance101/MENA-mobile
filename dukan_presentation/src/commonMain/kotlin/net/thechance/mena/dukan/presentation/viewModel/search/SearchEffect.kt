package net.thechance.mena.dukan.presentation.viewModel.search

sealed interface SearchEffect {
    object NavigateBack : SearchEffect
    data class NavigateToDukanDetails(val dukanId: String) : SearchEffect
    data class NavigateToProductDetails(val productId: String,val dukanId: String) : SearchEffect
}