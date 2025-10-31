package net.thechance.mena.faith.presentation.feature.mosque

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.thechance.mena.faith.presentation.base.BaseViewModel

internal class NearbyMosquesViewModel() :
    BaseViewModel<NearbyMosquesMapUiState, NearbyMosquesEffect>(
        NearbyMosquesMapUiState()
    ), NearbyMosquesInteractionListener {

    private var searchButtonInactivityJob: Job? = null

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

    override fun mapPositionChanged() = handleSearchButtonVisibilityOnInteraction()

    override fun onQueryChange(query: String) {
        updateState { it.copy(query = query) }
        handleSearchButtonVisibilityOnInteraction()
    }

    private fun handleSearchButtonVisibilityOnInteraction() {
        updateState { it.copy(isSearchButtonVisible = false) }
        searchButtonInactivityJob?.cancel()
        searchButtonInactivityJob = viewModelScope.launch {
            delay(500)
            updateState { it.copy(isSearchButtonVisible = true) }
        }
    }
}
