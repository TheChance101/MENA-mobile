package net.thechance.mena.dukan.presentation.viewModel.dukanLocation

import androidx.compose.ui.unit.DpOffset
import io.github.dellisd.spatialk.geojson.Position
import org.maplibre.compose.camera.CameraPosition


data class DukanLocationUiState(
    val dukanLocation: CoordinatesUiState = CoordinatesUiState(),
    val pointerLocation: DpOffset? = null,
    val cameraPosition: CameraPosition = CameraPosition(target = Position(29.0, 28.0), zoom = 1.0)
) {
    data class CoordinatesUiState(
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
    )
}