package net.thechance.mena.dukan.presentation.viewModel.dukanLocation

import androidx.compose.ui.unit.DpOffset
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState
import org.maplibre.compose.camera.CameraPosition

interface DukanLocationInteractionListener {
    fun onBackClicked()
    fun onMapClicked(
        coordinates: CreateDukanUiState.CoordinatesUiState,
        pointerLocation: DpOffset,
    )
    fun onCameraMoved(camera: CameraPosition)
}