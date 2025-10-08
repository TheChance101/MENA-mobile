package net.thechance.mena.identity.presentation.screen.pickLocation

import androidx.compose.ui.unit.DpOffset
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import org.maplibre.compose.camera.CameraPosition

class PickLocationScreenViewModel(): BaseScreenModel<PickLocationScreenUIState, PickLocationScreenUIEffect>(PickLocationScreenUIState()), PickLocationScreenInteractionListener {
    override fun onMapClicked(
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
                pointerLocation = pointerLocation
            )
        }
//        return locationRepository.getCurrentLocationName(coordinates.toEntity())
        return "hi"
    }

    private fun onMapClickedSuccess(address: String) {
        onAddressChanged(address)
    }

     fun onAddressChanged(address: String) {
        updateState { copy(address = address) }
//        updateNextButtonEnableState()
    }

    override fun onCameraMoved(
        camera: CameraPosition
    ) {
        updateState { copy(cameraPosition = camera) }
    }

    override fun onEditClick() {
        updateState {
            copy(
                address = "",
                currentLocation = PickLocationScreenUIState.CoordinatesUiState(),
                pointerLocation = null,
            )
        }
//        updateNextButtonEnableState()
    }

    override fun onGpsClick() {
        TODO("Not yet implemented")
    }

    override fun onConfirmClicked() {
        TODO("Not yet implemented")
    }

}