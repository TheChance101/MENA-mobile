package net.thechance.mena.identity.presentation.screen.addresses.pickLocation

import androidx.compose.ui.unit.DpOffset
import net.thechance.mena.identity.presentation.base.BaseInteractionListener
import org.maplibre.compose.camera.CameraPosition

interface PickLocationScreenInteractionListener : BaseInteractionListener {
    fun onClickMap(coordinates: PickLocationScreenUIState.CoordinatesUiState, offset: DpOffset)
    fun onMoveCamera(cameraPosition: CameraPosition)
    fun onClickEdit()
    fun onClickGps()
    fun onClickConfirm()
    fun onClearErrorMessage()
    fun onClickBack()
    fun onSetAnchorLocation(pointerLocation: DpOffset)
    fun onUpdateAddress(addressModel: AddressModel?)
}


