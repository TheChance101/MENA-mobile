package net.thechance.mena.faith.presentation.feature.mosque

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.thechance.mena.faith.domain.entity.Mosque
import net.thechance.mena.faith.domain.repository.MosqueRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.base.createPagingSourceFlow
import net.thechance.mena.faith.presentation.base.snackbar.SnackbarHandler
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.service.LocationService

internal class NearbyMosquesViewModel(
    private val mosqueRepository: MosqueRepository,
    private val locationService: LocationService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    snackbarHandler: SnackbarHandler
) : BaseViewModel<NearbyMosquesMapUiState, NearbyMosquesEffect>(
    initialState = NearbyMosquesMapUiState(),
    snackbarHandler = snackbarHandler,
), NearbyMosquesInteractionListener {


    init {
        getUserLocation()
    }

    override fun getUserLocation() {
        tryToExecute(
            execute = { locationService.getActiveAddress()!! },
            onSuccess = ::onGetUserLocationSuccess,
            onError = { sendEffect(NearbyMosquesEffect.NavigateToAddressesScreen) }
        )
    }

    private fun onGetUserLocationSuccess(address: Address) {
        updateState {
            it.copy(
                centerOfMap = Coordinate(address.latitude, address.longitude),
                mosquesSearchResults = createMosquesPagingSource(""),
                isLoading = false
            )
        }
    }

    private fun createMosquesPagingSource(query: String): Flow<PagingData<MosqueUiState>> {
        return if (query.isBlank()) {
            flow {
                val mosques = mosqueRepository.getNearbyMosques(
                    latitude = uiState.value.centerOfMap?.latitude ?: 0.0,
                    longitude = uiState.value.centerOfMap?.longitude ?: 0.0,
                    radius = 1.0
                )
                emit(
                    PagingData.from(
                        mosques.map { it.toUiState(0.0) }
                    )
                )
            }.cachedIn(viewModelScope)
        } else {
            createPagingSourceFlow { pageNumber, pageSize ->
                mosqueRepository.getMosquesByName(
                    query = query,
                    page = pageNumber,
                    size = pageSize
                )
            }.map { pagingData ->
                pagingData.map { mosque -> mosque.toUiState(0.0) }
            }.cachedIn(viewModelScope)
        }
    }


    override fun onQueryChange(query: String) {
        updateState { it.copy(query = query) }
    }

    override fun onSearchSubmit() {
        if (uiState.value.query.isBlank()) return

        tryToExecute(
            execute = { mosqueRepository.getMosquesByName(uiState.value.query) },
            onStart = { updateState { it.copy(isLoading = true) } },
            onSuccess = { mosques -> handleSearchSuccess(mosques, uiState.value.query) },
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

    override fun onViewMosqueDetailsClick(mosque: MosqueUiState) {
//        TODO("Not yet implemented")
    }
    private fun handleSearchSuccess(mosques: List<Mosque>, query: String) {
        if (mosques.isEmpty()&& !uiState.value.isSearchResultsBottomSheetVisible) {
            viewModelScope.launch {
                updateState {
                    it.copy(
                        isNoMosquesCardVisible = true,
                        isSearchResultsBottomSheetVisible = false,
                        isLoading = false
                    )
                }
                if (uiState.value.isNoMosquesCardVisible) {
                    delay(3000L)
                }
                updateState {
                    it.copy(
                        isNoMosquesCardVisible = false,
                    )
                }
            }
        } else {
            updateState {
                it.copy(
                    mosquesSearchResults = createMosquesPagingSource(query),
                    isNoMosquesCardVisible = false,
                    isSearchResultsBottomSheetVisible = true,
                    isLoading = false
                )
            }
        }
    }


    override fun onSearchByCoordinatesClick(coordinate: Coordinate) {
        updateState {
            it.copy(
                mosquesSearchResults = createMosquesPagingSource(uiState.value.query),
                isLoading = false,
            )
        }
    }

    override fun onSearchResultClick(mosque: MosqueUiState) {
        updateState {
            it.copy(
                isSearchResultsBottomSheetVisible = false,
                centerOfMap = mosque.coordinate,
            )
        }
    }

    override fun mapPositionChanged(coordinate: Coordinate) {
        updateState { it.copy(centerOfMap = coordinate) }
    }

    override fun changeSearchButtonVisibility(isVisible: Boolean) {
        updateState { it.copy(isSearchButtonVisible = isVisible) }
    }

    override fun onDismissSearchBottomSheet() {
        updateState { it.copy(isSearchResultsBottomSheetVisible = false) }
    }

    override fun selectMosque(mosque: MosqueUiState) {
        updateState {
            it.copy(
                selectedMosque = mosque,
                isMosqueBottomSheetVisible = true
            )
        }
    }

    override fun unselectMosque() {
        updateState {
            it.copy(
                selectedMosque = null,
                isMosqueBottomSheetVisible = false
            )
        }
    }

    override fun onViewOnMapClick(coordinate: Coordinate) {
        sendEffect(NearbyMosquesEffect.NavigateToMap(coordinate))
    }
}
