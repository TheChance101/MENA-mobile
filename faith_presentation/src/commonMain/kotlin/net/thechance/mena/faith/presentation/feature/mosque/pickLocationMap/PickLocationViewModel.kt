package net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.faith.domain.entity.Mosque
import net.thechance.mena.faith.domain.repository.LocationRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap.args.PickLocationArgs

internal class PickLocationViewModel(
    private val locationRepository: LocationRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val pickLocationArgs: PickLocationArgs
) : BaseViewModel<PickLocationScreenUIState, PickLocationScreenUIEffect>(
    PickLocationScreenUIState()
), PickLocationScreenInteractionListener {

    init {
        if (pickLocationArgs.latitude != null && pickLocationArgs.longitude != null) {
            initCoordinates()
        }
    }

    private fun initCoordinates() {
        updateState {
            it.copy(
                mosqueLocation = CoordinatesUiState(
                    latitude = pickLocationArgs.latitude!!,
                    longitude = pickLocationArgs.longitude!!
                ),
                animateToCurrentLocation = true,
                showAnchor = true,
            )
        }
        fetchLocationName()
    }

    override fun onClickMap(coordinates: CoordinatesUiState) {
        updateState {
            it.copy(
                mosqueLocation = CoordinatesUiState(
                    latitude = coordinates.latitude,
                    longitude = coordinates.longitude
                ),
                showAnchor = true
            )
        }
        fetchLocationName()
    }

    override fun onMoveCamera(coordinates: CoordinatesUiState) {
        updateState {
            it.copy(
                mosqueLocation = CoordinatesUiState(
                    latitude = coordinates.latitude,
                    longitude = coordinates.longitude
                ),
                showAnchor = true,
                animateToCurrentLocation = false
            )
        }
        fetchLocationName()
    }

    override fun onClickGps() {
        updateState { it.copy(isGpsButtonLoading = true) }
        tryToExecute(
            execute = { locationRepository.getCurrentLocation() },
            onSuccess = ::onGetCurrentLocationSuccess,
            onError = { error ->
                updateState { it.copy(isGpsButtonLoading = false) }
                handleErrorSnackBar(error)
            },
            dispatcher = dispatcher
        )
    }

    private fun onGetCurrentLocationSuccess(coordinates: Mosque.Coordinates?) {
        if (coordinates != null) {
            updateState {
                it.copy(
                    mosqueLocation = CoordinatesUiState(
                        latitude = coordinates.latitude,
                        longitude = coordinates.longitude
                    ),
                    animateToCurrentLocation = true,
                    showAnchor = true,
                    isGpsButtonLoading = false
                )
            }
            fetchLocationName()
        }
    }

    private fun fetchLocationName() {
        tryToExecute(
            execute = { getLocationName() },
            onSuccess = { address ->
                updateState {
                    it.copy(
                        address = address,
                        isConfirmEnabled = address.isNotBlank()
                    )
                }
            },
            onError = {
                updateState {
                    it.copy(
                        address = "",
                        isConfirmEnabled = false
                    )
                }
                handleErrorSnackBar(it)
            },
            dispatcher = dispatcher
        )
    }

    private suspend fun getLocationName(): String {
        return locationRepository.getLocationName(
            Mosque.Coordinates(
                latitude = uiState.value.mosqueLocation.latitude,
                longitude = uiState.value.mosqueLocation.longitude
            )
        )
    }

    override fun onClickConfirm() {
        sendEffect(
            PickLocationScreenUIEffect.NavigateBackWithLocation(
                AddressModel(
                    coordinates = CoordinatesUiState(
                        latitude = uiState.value.mosqueLocation.latitude,
                        longitude = uiState.value.mosqueLocation.longitude
                    ),
                    address = uiState.value.address
                )
            )
        )
    }

    override fun onClickBack() {
        sendEffect(PickLocationScreenUIEffect.NavigateBack)
    }
}
