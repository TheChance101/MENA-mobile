package net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap

import io.github.dellisd.spatialk.geojson.Position
import net.thechance.mena.identity.domain.model.Coordinates

data class PickLocationScreenUIState(
    val mosqueLocation: CoordinatesUiState = CoordinatesUiState(0.0, 0.0),
    val animateToCurrentLocation: Boolean = false,
    val showAnchor: Boolean = false,
    val address: String = "",
    val isConfirmEnabled: Boolean = false,
    val isGpsButtonLoading: Boolean = false
)

data class CoordinatesUiState(
    val latitude: Double,
    val longitude: Double
)

fun Coordinates.toUiState() = CoordinatesUiState(
    latitude = latitude,
    longitude = longitude
)

fun CoordinatesUiState.toPosition() = Position(
    latitude = latitude,
    longitude = longitude
)

fun Position.toCoordinatesUiState() = CoordinatesUiState(
    latitude = latitude,
    longitude = longitude
)