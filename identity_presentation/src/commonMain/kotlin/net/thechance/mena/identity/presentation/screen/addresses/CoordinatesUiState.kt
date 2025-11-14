package net.thechance.mena.identity.presentation.screen.addresses

import io.github.dellisd.spatialk.geojson.Position
import net.thechance.mena.identity.domain.model.Coordinates

data class CoordinatesUiState(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)

fun CoordinatesUiState.toEntity() = Coordinates(
    latitude = latitude,
    longitude = longitude
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
