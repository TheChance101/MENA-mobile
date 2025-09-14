package net.thechance.mena.dukan.presentation.viewModel.createDukan

import net.thechance.mena.dukan.domain.entity.Dukan

fun Dukan.Coordinates.toUiState() = CreateDukanUiState.CoordinatesUi(
    latitude = latitude,
    longitude = longitude,
)

fun CreateDukanUiState.CoordinatesUi.toEntity() = Dukan.Coordinates(
    latitude = latitude,
    longitude = longitude,
)