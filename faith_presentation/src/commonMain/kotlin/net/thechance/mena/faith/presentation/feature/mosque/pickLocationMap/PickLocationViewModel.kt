package net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap.args.PickLocationArgs
import net.thechance.mena.identity.domain.model.Coordinates
import net.thechance.mena.identity.domain.repository.AddressesRepository

class PickLocationViewModel(
    private val addressesRepository: AddressesRepository,
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
        tryToExecute(
            execute = { addressesRepository.getCurrentLocation() },
            onSuccess = { coordinates ->
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
            },
            onError = { error ->
                updateState { it.copy(isGpsButtonLoading = false) }
                handleErrorSnackBar(error)
            },
            dispatcher = dispatcher
        )
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
        return addressesRepository.getLocationName(
            Coordinates(
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
