package net.thechance.mena.faith.presentation.feature.mosque

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.no_mosques_found_by_keyword
import net.thechance.mena.faith.domain.entity.Mosque
import net.thechance.mena.faith.domain.repository.MosqueRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.base.snackbar.SnackBarState
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.service.LocationService

internal class NearbyMosquesViewModel(
    private val mosqueRepository: MosqueRepository,
    private val locationService: LocationService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<NearbyMosquesMapUiState, NearbyMosquesEffect>(
    initialState = NearbyMosquesMapUiState()
), NearbyMosquesInteractionListener {

    init {
        getUserLocation()
    }

    private var searchJob: Job? = null
    private var userCoordinate: Coordinate? = null

    private fun getUserLocation() {
        tryToExecute(
            execute = { locationService.getActiveAddress()!! },
            onSuccess = ::onGetUserLocationSuccess,
            onError = { sendEffect(NearbyMosquesEffect.NavigateToAddressesScreen) }
        )
    }

    private fun onGetUserLocationSuccess(address: Address) {
        userCoordinate = Coordinate(address.latitude, address.longitude)
        tryToExecute(
            execute = {
                mosqueRepository.getNearbyMosques(
                    latitude = address.latitude,
                    longitude = address.longitude,
                    radius = 20.0
                )
            },
            onStart = { updateState { it.copy(isLoading = true) } },
            onSuccess = { mosques ->
                userCoordinate?.let { coord ->
                    handleNearbyMosquesSuccess(mosques, coord)
                }
            },
            onFinally = { updateState { it.copy(isLoading = false) } },
            dispatcher = dispatcher
        )
    }

    override fun onBackClick() {}

    override fun onAddMosqueClick() {}

    override fun onCurrentUserLocationClick() {}

    override fun onViewMosqueDetailsClick(mosque: MosqueUiState) {}

    override fun onViewMosqueOnMapClick(coordinate: Coordinate) {}

    override fun onSearchByCoordinatesClick(coordinate: Coordinate) {
        val center = uiState.value.centerOfMap ?: return
        updateState { it.copy(isLoading = true) }
        tryToExecute(
            dispatcher = dispatcher,
            execute = {
                mosqueRepository.getNearbyMosques(
                    latitude = center.latitude,
                    longitude = center.longitude,
                    radius = SEARCH_RADIUS_KM
                )
            },
            onSuccess = { mosques ->
                userCoordinate?.let { coord ->
                    handleNearbyMosquesSuccess(mosques, coord)
                }
            }
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
                    isNoMosquesCardVisible = false,
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
        val currentUser = userCoordinate
        updateState { it.copy(isLoading = true) }
        searchJob = tryToExecute(
            execute = { mosqueRepository.getMosquesByName(query) },
            onSuccess = { mosques ->
                currentUser?.let { coord ->
                    handleSearchSuccess(mosques, coord)
                }
            },
            onError = {
                handleSearchError()
                updateState { it.copy(isLoading = false) }
            },
            dispatcher = dispatcher,
            delayMillis = SEARCH_DEBOUNCE_DELAY
        )
    }


    private fun handleNearbyMosquesSuccess(mosques: List<Mosque>, userLocation: Coordinate) {
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
                        mosque.toUiState(0.0)
                    }
                )
            }
        }
    }

    private fun handleSearchSuccess(mosques: List<Mosque>, userLocation: Coordinate) {
        updateState {
            it.copy(
                mosquesSearchResults = mosques.map { mosque ->
                    mosque.toUiState(0.0)
                },
                isSearchResultsBottomSheetVisible = mosques.isNotEmpty(),
                isLoading = false,
                isNoMosquesCardVisible = mosques.isEmpty()
            )
            // TODO: remove all markers from the map and add new markers
        }
    }

    private fun handleSearchError() =
        snackbarHandler.showSnackBar(
            message = Res.string.no_mosques_found_by_keyword,
            status = SnackBarState.Status.Error,
            scope = viewModelScope,
        )

    private companion object {
        const val SEARCH_DEBOUNCE_DELAY = 1000L
        const val SEARCH_RADIUS_KM = 1.0
    }
}