package net.thechance.mena.identity.presentation.screen.pickLocation

import androidx.compose.ui.unit.DpOffset
import net.thechance.mena.identity.domain.entity.Coordinates
import net.thechance.mena.identity.domain.repository.LocationRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.ErrorState
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import org.maplibre.compose.camera.CameraPosition

class PickLocationScreenViewModel(
    private val locationRepository: LocationRepository
) : BaseScreenModel<PickLocationScreenUIState, PickLocationScreenUIEffect>(PickLocationScreenUIState()),
    PickLocationScreenInteractionListener {
    override fun onClickMap(
        coordinates: PickLocationScreenUIState.CoordinatesUiState,
        pointerLocation: DpOffset
    ) {
        tryToExecute(
            function = { onMapClickedBlock(coordinates, pointerLocation) },
            onSuccess = ::onMapClickedSuccess,
            onError = ::onError
        )
    }

    private suspend fun onMapClickedBlock(
        coordinates: PickLocationScreenUIState.CoordinatesUiState,
        pointerLocation: DpOffset
    ): String {
        updateState {
            copy(
                currentLocation = coordinates,
                pointerLocation = pointerLocation,
                isMapLocked = true
            )
        }
        return locationRepository.getLocationName(coordinates.toEntity())
    }

    private fun onMapClickedSuccess(address: String) {
        updateState { copy(address = address) }
        changeIsConfirmEnabled()
    }

    override fun onCameraMoved(
        cameraPosition: CameraPosition
    ) {
        updateState { copy(cameraPosition = cameraPosition, animateToCurrentLocation = false) }
    }

    override fun onClickEdit() {
        updateState {
            copy(
                address = "",
                currentLocation = PickLocationScreenUIState.CoordinatesUiState(),
                pointerLocation = null,
                isMapLocked = false,
                animateToCurrentLocation = false
            )
        }
        changeIsConfirmEnabled()
    }

    override fun onClickGps() {
        tryToExecute(
            function = ::onGpsFetch,
            onSuccess = ::onGpsClickSuccess,
            onError = ::onError
        )
    }

    private suspend fun onGpsFetch(): Coordinates? {
        updateState { copy(isGpsButtonLoading = true) }
        return locationRepository.getCurrentLocation()
    }

    private fun onError(errorState: ErrorState) {
        updateState {
            copy(
                errorMessage = mapErrorToMessage(errorState),
                isGpsButtonLoading = false
            )
        }
    }

    private fun onGpsClickSuccess(
        coordinates: Coordinates?
    ) {
        if (coordinates != null) {
            updateState {
                copy(
                    currentLocation = coordinates.toUiState(),
                    isMapLocked = true,
                    animateToCurrentLocation = true,
                    isGpsButtonLoading = false
                )
            }
        }
    }

    override fun onClickConfirm() {
        sendNewEffect(
            PickLocationScreenUIEffect.NavigateToAddLocation(
                latitude = state.value.currentLocation.latitude,
                longitude = state.value.currentLocation.longitude,
                address = state.value.address
            )
        )
    }

    override fun onClearErrorMessage() {
        updateState { copy(errorMessage = null) }
    }

    override fun onClickBack() {
        sendNewEffect(PickLocationScreenUIEffect.NavigateBack)
    }

    private fun changeIsConfirmEnabled() {
        if (state.value.isMapLocked && state.value.address.isNotBlank()) {
            updateState { copy(isConfirmEnabled = true) }
        } else {
            updateState { copy(isConfirmEnabled = false) }
        }
    }
}