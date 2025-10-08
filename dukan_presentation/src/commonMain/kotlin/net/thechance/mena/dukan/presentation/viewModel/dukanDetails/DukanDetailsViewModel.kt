package net.thechance.mena.dukan.presentation.viewModel.dukanDetails

import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel

class DukanDetailsViewModel :
    BaseViewModel<DukanDetailsUiState, DukanDetailsEffects>(DukanDetailsUiState()),
    DukanDetailsInteractionListener {
    override fun onBackClicked() {
        emitEffect(DukanDetailsEffects.NavigateBack)
    }

    override fun onShelfClicked(id: String) {
        TODO("Not yet implemented")
    }

    override fun onViewAllShelfProductsClicked(id: String, name: String) {
        emitEffect(DukanDetailsEffects.NavigateToViewAllShelfProducts(id, name))
    }

    override fun onViewDukanOnMapClicked(latitude: Double, longitude: Double) {
        emitEffect(DukanDetailsEffects.NavigateToViewDukanOnMap(latitude, longitude))
    }
}