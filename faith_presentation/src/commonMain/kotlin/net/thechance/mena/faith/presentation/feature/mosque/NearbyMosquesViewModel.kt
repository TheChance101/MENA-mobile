package net.thechance.mena.faith.presentation.feature.mosque

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.thechance.mena.faith.domain.entity.Mosque
import net.thechance.mena.faith.domain.repository.MosqueRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.service.LocationService

internal class NearbyMosquesViewModel(
    private val mosqueRepository: MosqueRepository,
    private val locationService: LocationService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) :
    BaseViewModel<NearbyMosquesMapUiState, NearbyMosquesEffect>(
        initialState = NearbyMosquesMapUiState(),
    ), NearbyMosquesInteractionListener {

    init {
        getUserLocation()
    }

    private var searchJob: Job? = null

    private fun getUserLocation() {
        tryToExecute(
            execute = { locationService.getActiveAddress()!! },
            onSuccess = ::onGetUserLocationSuccess,
            onError = { sendEffect(NearbyMosquesEffect.NavigateToAddressesScreen) }
        )
    }

    private fun onGetUserLocationSuccess(address: Address) {
        tryToExecute(
            execute = {
                mosqueRepository.getNearbyMosques(
                    latitude = address.latitude,
                    longitude = address.longitude,
                    radius = 1.0
                )
            },
            onStart = { updateState { it.copy(isLoading = true) } },
            onSuccess = ::handleNearbyMosquesSuccess,
            onFinally = { updateState { it.copy(isLoading = false) } },
            dispatcher = dispatcher

        )
    }

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
        val center = uiState.value.centerOfMap ?: return
        updateState { it.copy(isLoading = true) }
        tryToExecute(
            dispatcher = dispatcher,
            execute = {
                mosqueRepository.getNearbyMosques(
                    latitude = center.latitude,
                    longitude = center.longitude,
                    radius = SEARCH_RADIUS_KM,
                )
            },
            onSuccess = ::handleNearbyMosquesSuccess,
        )
    }

    override fun onSearchResultClick(mosque: MosqueUiState) {
        updateState {
            it.copy(
                isSearchResultsBottomSheetVisible = false,
                centerOfMap = mosque.coordinate
            )
        }
    }

    override fun mapPositionChanged(coordinate: Coordinate) {
        updateState { it.copy(centerOfMap = coordinate) }
    }

    override fun onQueryChange(query: String) {
        updateState { it.copy(query = query) }
        searchJob?.cancel()
        if (query.isBlank()) {
            updateState {
                it.copy(
                    mosquesSearchResults = emptyList(),
                    isSearchResultsBottomSheetVisible = false
                )
            }
        } else {
            performSearch(query)
        }
    }

    override fun changeSearchButtonVisibility(isVisible: Boolean) {
        updateState { it.copy(isSearchButtonVisible = isVisible) }
    }

    override fun onDismissSearchBottomSheet() {
        updateState { it.copy(isSearchResultsBottomSheetVisible = false) }
    }

    private fun performSearch(query: String) {
        searchJob = tryToExecute(
            execute = { mosqueRepository.getMosquesByName(query) },
            onSuccess = ::handleSearchSuccess,
            onError = { handleSearchError() },
            dispatcher = dispatcher,
            delayMillis = SEARCH_DEBOUNCE_DELAY,
        )
    }

    private fun handleNearbyMosquesSuccess(mosques: List<Mosque>) {
        if (mosques.isEmpty()) {
            viewModelScope.launch {
                updateState { it.copy(isNoMosquesCardVisible = true) }
                delay(3000)
                updateState { it.copy(isNoMosquesCardVisible = false) }
            }
        } else {
            updateState {
                it.copy(
                    isLoading = false,
                    mosques = mosques.map { mosque ->
                        mosque.toUiState(distance = 0.0)
                    },
                )
            }
        }
    }

    private fun handleSearchSuccess(mosques: List<Mosque>) {
        updateState {
            it.copy(
                mosquesSearchResults = mosques.map { mosque ->
                    mosque.toUiState(0.0)
                },
                isSearchResultsBottomSheetVisible = mosques.isNotEmpty()
            )
        }
        // TODO: remove all markers from the map and add new markers
    }

    private fun handleSearchError() {
        // TODO: show snack bar with error message (Res.string.no_mosques_found) to the user
    }

    private companion object {
        const val SEARCH_DEBOUNCE_DELAY = 1000L
        const val SEARCH_RADIUS_KM = 1.0
    }
}
