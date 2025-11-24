package net.thechance.mena.faith.presentation.utils

import net.thechance.mena.faith.presentation.feature.mosque.MosqueUiState


internal expect class MapNavigatorImpl : MapNavigator {
    override fun openMapAtCoordinate(coordinate: MosqueUiState.Coordinate)
}

internal interface MapNavigator {
    fun openMapAtCoordinate(coordinate: MosqueUiState.Coordinate)
}