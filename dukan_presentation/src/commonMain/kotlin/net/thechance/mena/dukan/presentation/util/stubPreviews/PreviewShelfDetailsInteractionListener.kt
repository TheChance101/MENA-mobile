package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.shelfDetails.ShelfDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.shelfDetails.ShelfDetailsUiState

object PreviewShelfDetailsInteractionListener : ShelfDetailsInteractionListener {
    override fun onBackClicked() {}
    override fun onAddToCartClicked(product: ShelfDetailsUiState.ProductUiState) {}
    override fun onPlusClicked(product: ShelfDetailsUiState.ProductUiState) {}
    override fun onMinusClicked(product: ShelfDetailsUiState.ProductUiState) {}
    override fun onCartClicked() {}
}