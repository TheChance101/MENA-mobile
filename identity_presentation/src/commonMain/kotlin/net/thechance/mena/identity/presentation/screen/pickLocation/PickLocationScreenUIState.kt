package net.thechance.mena.identity.presentation.screen.pickLocation

import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import io.github.dellisd.spatialk.geojson.Position
import org.maplibre.compose.camera.CameraPosition

data class PickLocationScreenUIState (
    val pointerLocation: DpOffset? = DpOffset(0.dp, 0.dp),
    val cameraPosition: CameraPosition = CameraPosition(
        target = Position(20.31852, 20.44519),
        zoom = 15.0
    ),

    val currentLocation: CoordinatesUiState = CoordinatesUiState(),
    val address: String = "",
    val isButtonEnabled: Boolean = false,
    val isMapLocked: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isConfirmEnabled: Boolean = false,

) {
    data class CoordinatesUiState(
        val latitude: Double = 5.0,
        val longitude: Double = 5.0,
    )
}

