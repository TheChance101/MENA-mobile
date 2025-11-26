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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.thechance.mena.faith.domain.entity.Mosque
import net.thechance.mena.faith.domain.repository.MosqueRepository
import net.thechance.mena.faith.domain.usecase.CalculateDistanceUseCase
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.base.createPagingSourceFlow
import net.thechance.mena.faith.presentation.utils.extentions.roundTo2Decimals
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.service.LocationService
import org.jetbrains.compose.resources.StringResource

internal class NearbyMosquesViewModel(
    private val mosqueRepository: MosqueRepository,
    private val locationService: LocationService,
    private val calculateDistanceUseCase: CalculateDistanceUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<NearbyMosquesMapUiState, NearbyMosquesEffect>(
    initialState = NearbyMosquesMapUiState(),
), NearbyMosquesInteractionListener {
    init {
        getUserLocation()
    }

    override fun getUserLocation() {
        tryToExecute(
            execute = { locationService.getActiveAddress()!! },
            onSuccess = ::onGetUserLocationSuccess,
            onError = {
                sendEffect(NearbyMosquesEffect.NavigateToAddressesScreen)
            }
        )
    }

    private fun onGetUserLocationSuccess(address: Address) {
        updateState {
            it.copy(
                userLocation = MosqueUiState.Coordinate(address.latitude, address.longitude),
                centerOfMap = MosqueUiState.Coordinate(address.latitude, address.longitude),
                canMove = true,
                isLoading = false
            )
        }
        onSearchByCoordinates(MosqueUiState.Coordinate(address.latitude, address.longitude))
    }

    private fun createMosquesPagingSource(query: String): Flow<PagingData<MosqueUiState>> {
        return createPagingSourceFlow { pageNumber, pageSize ->
            mosqueRepository.getMosquesByName(query, pageNumber, pageSize)
        }.map { pagingData ->
            pagingData.map { mosque ->
                mosque.toUiState(
                    getDistanceFromUser(mosque.coordinates)
                )
            }
        }.cachedIn(viewModelScope)
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
            onError = ::handleErrorSnackBar,
            onFinally = { updateState { it.copy(isLoading = false) } },
            dispatcher = dispatcher
        )
    }

    override fun onBackClick() {
        sendEffect(NearbyMosquesEffect.NavigateBack)
    }

    override fun onAddMosqueClick() {
        sendEffect(NearbyMosquesEffect.NavigateToAddMosque)
    }

    override fun onViewMosqueDetailsClick(mosque: MosqueUiState) {
//        TODO("Not yet implemented")
    }

    private fun handleSearchSuccess(mosques: List<Mosque>, query: String) {
        if (mosques.isEmpty() && !uiState.value.isSearchResultsBottomSheetVisible) {
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

    override fun onSearchByCoordinates(coordinate: MosqueUiState.Coordinate) {
        tryToExecute(
            execute = {
                mosqueRepository.getNearbyMosques(
                    latitude = coordinate.latitude,
                    longitude = coordinate.longitude,
                )
            },
            onStart = { updateState { it.copy(isLoading = true) } },
            onSuccess = ::handleNearbyMosquesSuccess,
            onError = { error ->
                updateState { it.copy(isLoading = false) }
                handleErrorSnackBar(error)
            },
            dispatcher = dispatcher
        )
    }

    private fun handleNearbyMosquesSuccess(mosques: List<Mosque>) {
        val mosquesWithDistance = mosques.map { mosque ->
            mosque.toUiState(getDistanceFromUser(mosque.coordinates))
        }
        updateState {
            it.copy(
                mosques = mosquesWithDistance,
                isLoading = false,
                selectedMosque = null
            )
        }
    }

    override fun onSearchResultClick(mosque: MosqueUiState) {
        updateState {
            it.copy(
                mosques = listOf(mosque),
                isSearchResultsBottomSheetVisible = false,
                centerOfMap = mosque.coordinate,
                canMove = true,
                selectedMosque = mosque,
            )
        }
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

    override fun showSuccessMessage(message: StringResource) {
        handleSuccessSnackBar(message)
    }

    override fun onCameraMove() {
        updateState { it.copy(isSearchButtonVisible = false) }
    }

    override fun onMapIdle(latitude: Double, longitude: Double) {
        updateState {
            it.copy(
                isSearchButtonVisible = true,
                canMove = false,
                centerOfMap = MosqueUiState.Coordinate(
                    latitude = latitude,
                    longitude = longitude
                )
            )
        }
    }

    override fun onViewOnMapClick(coordinate: MosqueUiState.Coordinate) {
        sendEffect(NearbyMosquesEffect.NavigateToMap(coordinate))
    }

    private fun getDistanceFromUser(coordinates: Mosque.Coordinates) =
        uiState.value.userLocation?.let { userLocation ->
            calculateDistanceUseCase(
                firstLocation = Mosque.Coordinates(
                    latitude = userLocation.latitude,
                    longitude = userLocation.longitude
                ),
                secondLocation = Mosque.Coordinates(
                    latitude = coordinates.latitude,
                    longitude = coordinates.longitude
                )
            )
        }?.roundTo2Decimals() ?: 0.0
}
