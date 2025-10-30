package net.thechance.mena.faith.presentation.feature.mosque

import net.thechance.mena.faith.presentation.base.BaseViewModel

internal class NearbyMosquesViewModel :
    BaseViewModel<NearbyMosquesMapUiState, NearbyMosquesEffect>(
        NearbyMosquesMapUiState()
    ), NearbyMosquesInteractionListener {

    override fun onBackClick() {
//        TODO("Not yet implemented")
    }

    override fun onAddMosqueClick() {
//        TODO("Not yet implemented")
    }

    override fun onCurrentUserLocationClick() {
//        TODO("Not yet implemented")
    }

    override fun onViewMosqueDetailsClick(mosque: MosqueUiState) {
//        TODO("Not yet implemented")
    }

    override fun onViewMosqueOnMapClick(latitude: Double, longitude: Double) {
//        TODO("Not yet implemented")
    }

    override fun onSearchByCoordinatesClick(latitude: Double, longitude: Double) {
//        TODO("Not yet implemented")
    }

    override fun mapPositionChanged() {
//        TODO("Not yet implemented")
    }

    override fun onQueryChange(query: String) {
//        TODO("Not yet implemented")
    }
}