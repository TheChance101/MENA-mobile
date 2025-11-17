package net.thechance.mena.identity.presentation.screen.addresses.shared

import io.github.dellisd.spatialk.geojson.Position
import net.thechance.mena.identity.domain.model.Coordinates

data class CoordinatesUiState(
    val latitude: Double = 29.203231755958047,
    val longitude: Double = 22.39869322710709,
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
