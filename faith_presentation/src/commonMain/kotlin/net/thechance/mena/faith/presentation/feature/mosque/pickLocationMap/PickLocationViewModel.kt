package net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.identity.domain.model.Coordinates
import net.thechance.mena.identity.domain.repository.AddressesRepository

class PickLocationViewModel(
    private val addressesRepository: AddressesRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<PickLocationScreenUIState, PickLocationScreenUIEffect>(
    PickLocationScreenUIState()
), PickLocationScreenInteractionListener {

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
                latitude = uiState.value.mosqueLocation?.latitude ?: 0.0,
                longitude = uiState.value.mosqueLocation?.longitude ?: 0.0
            )
        )
    }

    override fun onClickConfirm() {
        sendEffect(
            PickLocationScreenUIEffect.NavigateBackWithLocation(
                CoordinatesUiState(
                    latitude = uiState.value.mosqueLocation?.latitude ?: 0.0,
                    longitude = uiState.value.mosqueLocation?.longitude ?: 0.0
                )
            )
        )
    }

    override fun onClickBack() {
        sendEffect(PickLocationScreenUIEffect.NavigateBack)
    }
}
