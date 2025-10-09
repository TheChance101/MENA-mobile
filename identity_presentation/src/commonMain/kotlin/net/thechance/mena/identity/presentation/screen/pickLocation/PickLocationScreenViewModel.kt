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
            onError = {}
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
        onAddressChanged(address)
    }

    fun onAddressChanged(address: String) {
        updateState { copy(address = address) }
        changeIsConfirmEnabled()
    }

    override fun onCameraMoved(
        camera: CameraPosition
    ) {
        updateState { copy(cameraPosition = camera, animateToCurrentLocation = false) }
    }

    override fun onClickEdit() {
        updateState {
            copy(
                address = "",
                currentLocation = PickLocationScreenUIState.CoordinatesUiState(),
                pointerLocation = null,
                isMapLocked = false
            )
        }
        changeIsConfirmEnabled()
    }

    override fun onClickGps() {
        tryToExecute(
            function = { locationRepository.getCurrentLocation() },
            onSuccess = ::onGpsClickSuccess,
            onError = ::onError
        )
    }

    private fun onError(errorState: ErrorState) {
        updateState { copy(errorMessage = mapErrorToMessage(errorState)) }
    }

    private fun onGpsClickSuccess(
        coordinates: Coordinates?
    ) {
        updateState {
            copy(
                currentLocation = coordinates?.toUiState(),
                pointerLocation = null,
                animateToCurrentLocation = true
            )
        }
    }

    fun Coordinates.toUiState() = PickLocationScreenUIState.CoordinatesUiState(
        latitude = latitude,
        longitude = longitude
    )

    override fun onClickConfirm() {
        TODO("Not yet implemented")
    }

    override fun onClearErrorMessage() {
        updateState { copy(errorMessage = null) }
    }

    private fun changeIsConfirmEnabled() {
        if (state.value.isMapLocked) {
            updateState { copy(isConfirmEnabled = true) }
        } else {
            updateState { copy(isConfirmEnabled = false) }
        }
    }
}

fun PickLocationScreenUIState.CoordinatesUiState.toEntity() = Coordinates(
    latitude = latitude,
    longitude = longitude
)

