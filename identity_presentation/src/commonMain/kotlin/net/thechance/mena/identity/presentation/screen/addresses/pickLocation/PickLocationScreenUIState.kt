package net.thechance.mena.identity.presentation.screen.addresses.pickLocation

import androidx.compose.ui.unit.DpOffset
import net.thechance.mena.identity.domain.model.Coordinates
import org.jetbrains.compose.resources.StringResource
import org.maplibre.compose.camera.CameraPosition

data class PickLocationScreenUIState(
    val pointerLocation: DpOffset? = null,
    val cameraPosition: CameraPosition = CameraPosition(),
    val animateToCurrentLocation: Boolean = false,
    val currentLocation: CoordinatesUiState = CoordinatesUiState(),
    val address: String = "",
    val isMapLocked: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: StringResource? = null,
    val isConfirmEnabled: Boolean = false,
    val isGpsButtonLoading: Boolean = false,
    val isMainAddress: Boolean = false
) {
    data class CoordinatesUiState(
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
    )
}

fun PickLocationScreenUIState.CoordinatesUiState.toEntity() = Coordinates(
    latitude = latitude,
    longitude = longitude
)

fun Coordinates.toUiState() = PickLocationScreenUIState.CoordinatesUiState(
    latitude = latitude,
    longitude = longitude
)
