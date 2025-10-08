package net.thechance.mena.identity.presentation.screen.pickLocation

import androidx.compose.ui.unit.DpOffset
import net.thechance.mena.identity.presentation.base.BaseInteractionListener
import org.maplibre.compose.camera.CameraPosition

interface PickLocationScreenInteractionListener : BaseInteractionListener {
    fun onMapClicked(coordinates: PickLocationScreenUIState.CoordinatesUiState, offset: DpOffset)
    fun onCameraMoved(cameraPosition: CameraPosition)
    fun onEditClick()
    fun onGpsClick()
    fun onConfirmClicked()
}


