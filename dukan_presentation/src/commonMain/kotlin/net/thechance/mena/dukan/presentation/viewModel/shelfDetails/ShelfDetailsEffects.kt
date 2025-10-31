package net.thechance.mena.dukan.presentation.viewModel.shelfDetails

import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsEffects

sealed class ShelfDetailsEffects {
    object NavigateBack : ShelfDetailsEffects()
    data class NavigateToProductDetails(val productId: String) : ShelfDetailsEffects()
}