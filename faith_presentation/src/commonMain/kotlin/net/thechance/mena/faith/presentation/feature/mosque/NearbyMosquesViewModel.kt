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
import kotlin.uuid.ExperimentalUuidApi

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

    private var searchJob: Job? = null


    private fun getNearbyMosques() {
        tryToExecute(
            execute = {
                val address = getValidatedAddress() ?: return@tryToExecute null
                mosqueRepository.getNearbyMosques(
                    latitude = address.latitude,
                    longitude = address.longitude,
                    radius = 1.0
                )
            },
            onStart = { updateState { it.copy(isLoading = true) } },
            onSuccess = { mosques -> mosques?.let { mosque -> handleNearbyMosquesSuccess(mosque) } },
            onFinally = { updateState { it.copy(isLoading = false) } },
            dispatcher = dispatcher

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
                    latitude = address.latitude,
                    longitude = address.longitude
                )
            )
        }
        return address
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

    @OptIn(ExperimentalUuidApi::class)
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

    private fun handleInvalidAddress() = sendEffect(NearbyMosquesEffect.NavigateToAddressesScreen)

    private companion object {
        const val SEARCH_DEBOUNCE_DELAY = 1000L
        const val SEARCH_RADIUS_KM = 1.0
    }
}
