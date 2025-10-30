package net.thechance.mena.faith.presentation.feature.mosque

import net.thechance.mena.faith.presentation.base.BaseViewModel

internal class MosqueMapViewModel : BaseViewModel<MosqueMapUiState, MosqueMapEffect>(
    MosqueMapUiState()
), MosqueMapInteractionListener {

    override fun onClickBack() {
//        TODO("Not yet implemented")
    }

    override fun onClickAddMosque() {
//        TODO("Not yet implemented")
    }

    override fun onClickCurrentUserLocation() {
//        TODO("Not yet implemented")
    }

    override fun onClickViewMosqueDetails(mosque: MosqueUiState) {
//        TODO("Not yet implemented")
    }

    override fun onClickViewMosqueOnMap(latitude: Double, longitude: Double) {
//        TODO("Not yet implemented")
    }

    override fun onClickSearchByCoordinates(latitude: Double, longitude: Double) {
//        TODO("Not yet implemented")
    }

    override fun mapPositionChanged() {
//        TODO("Not yet implemented")
    }

    override fun onQueryChange(query: String) {
//        TODO("Not yet implemented")
    }
}