package net.thechance.mena.faith.presentation.feature.mosque

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.thechance.mena.faith.presentation.base.BaseViewModel

internal class NearbyMosquesViewModel(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) :
    BaseViewModel<NearbyMosquesMapUiState, NearbyMosquesEffect>(
        initialState = NearbyMosquesMapUiState(),
    ), NearbyMosquesInteractionListener {

    private var searchButtonInactivityJob: Job? = null
    private var searchJob: Job? = null

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

    override fun onViewMosqueOnMapClick(coordinate: Coordinate) {
//        TODO("Not yet implemented")
    }

    override fun onSearchByCoordinatesClick(coordinate: Coordinate) {
//        TODO("Not yet implemented")
    }

    override fun mapPositionChanged(coordinate: Coordinate) {
        updateCenterOfMap(coordinate = coordinate)
        handleSearchButtonVisibilityOnInteraction()
    }

    override fun onQueryChange(query: String) {
        updateState { it.copy(query = query) }
        searchJob?.cancel()
        searchJob = tryToExecute(
            execute = {
                // TODO: send search request to the repository with the current query
            },
            onSuccess = { handleSearchSuccess() },
            onError = { handleSearchError() },
            dispatcher = dispatcher,
            delayMillis = SEARCH_DEBOUNCE_DELAY,
        )
    }

    private fun handleSearchSuccess() {
        updateState { it.copy(error = null) }
        // TODO: remove all markers from the map and add new markers
    }

    private fun handleSearchError() {
        // TODO: show snack bar with error message (Res.string.no_mosques_found) to the user
    }

    private fun handleSearchButtonVisibilityOnInteraction() {
        updateState { it.copy(isSearchButtonVisible = false) }
        searchButtonInactivityJob?.cancel()
        searchButtonInactivityJob = viewModelScope.launch {
            delay(500)
            updateState { it.copy(isSearchButtonVisible = true) }
        }
    }

    private fun updateCenterOfMap(coordinate: Coordinate) {
        updateState {
            it.copy(centerOfMap = coordinate)
        }
    }

    private companion object {
        const val SEARCH_DEBOUNCE_DELAY = 1000L
    }
}
