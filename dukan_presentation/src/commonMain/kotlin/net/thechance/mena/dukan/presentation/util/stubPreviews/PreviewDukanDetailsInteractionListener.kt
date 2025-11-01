package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState

object PreviewDukanDetailsInteractionListener : DukanDetailsInteractionListener {
    override fun onBackClicked() {}
    override fun onShelfClicked(id: String) {}
    override fun onViewAllProductsShelfClicked(id: String, name: String) {}
    override fun onViewDukanOnMapClicked(latitude: Double, longitude: Double) {}
    override fun onAddToCartClicked(product: DukanDetailsUiState.ProductUiState) {}
    override fun onPlusClicked(product: DukanDetailsUiState.ProductUiState) {}
    override fun onMinusClicked(product: DukanDetailsUiState.ProductUiState) {}
    override fun onCartClicked() {}
    override fun onRetryClicked() {}
}