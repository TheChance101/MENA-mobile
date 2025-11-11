package net.thechance.mena.dukan.presentation.viewModel.dukanLocation

import io.github.dellisd.spatialk.geojson.Position
import org.maplibre.compose.camera.CameraPosition


data class DukanLocationUiState(
    val cameraPosition: CameraPosition = CameraPosition(target = Position(29.0, 28.0), zoom = 1.0)
)