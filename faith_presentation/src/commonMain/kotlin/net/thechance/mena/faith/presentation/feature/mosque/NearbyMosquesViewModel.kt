package net.thechance.mena.faith.presentation.feature.mosque

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
        getNearbyMosques()
    }

    private var searchButtonInactivityJob: Job? = null
    private var searchJob: Job? = null


    private fun getNearbyMosques() {
        tryToExecute(
            execute = {
                val address = getValidatedAddress() ?: return@tryToExecute null
                mosqueRepository.getNearbyMosques(address.latitude, address.longitude, 1.0)
            },
            onStart = { updateState { it.copy(isLoading = true) } },
            onSuccess = { mosques ->
                mosques?.let { mosque ->
                    handleNearbyMosquesSuccess(mosque.map {
                        it.toUiState(0.0)
                    })
                }
            },
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

    override fun onCurrentUserLocationClick() = handleInvalidAddress()

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

    private fun performSearch(query: String) {
        searchJob = tryToExecute(
            execute = {
                mosqueRepository.getMosquesByName(query).map { it.toUiState(0.0) }
            },
            onSuccess = ::handleSearchSuccess,
            onError = { handleSearchError() },
            dispatcher = dispatcher,
            delayMillis = SEARCH_DEBOUNCE_DELAY,
        )
    }

    private suspend fun getValidatedAddress(): Address? {
        val address = locationService.getActiveAddress()

        if (address == null || address.addressLine.isEmpty()) {
            handleInvalidAddress()
            return null
        }

        updateState { state ->
            state.copy(
                centerOfMap = Coordinate(
                    address.latitude,
                    address.longitude
                )
            )
        }
        return address
    }

    private fun handleNearbyMosquesSuccess(mosques: List<MosqueUiState>) {
        if (mosques.isEmpty()) {
            viewModelScope.launch {
                updateState { it.copy(isNoMosquesCardVisible = true) }
                delay(3000)
                updateState { it.copy(isNoMosquesCardVisible = false) }
            }
        } else {
            // TODO: handle non-empty list case
        }
    }

    private fun handleSearchSuccess(mosques: List<MosqueUiState>) {
        updateState {
            it.copy(
                mosquesSearchResults = mosques,
                isSearchResultsBottomSheetVisible = mosques.isNotEmpty()
            )
        }
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

    private fun handleInvalidAddress() = sendEffect(NearbyMosquesEffect.NavigateToAddressesScreen)

    private companion object {
        const val SEARCH_DEBOUNCE_DELAY = 1000L
    }
}
