package net.thechance.mena.faith.presentation.utils

import net.thechance.mena.faith.presentation.feature.mosque.Coordinate

internal expect class MapNavigatorImpl : MapNavigator {
    override fun openMapAtCoordinate(coordinate: Coordinate)
}

internal interface MapNavigator {
    fun openMapAtCoordinate(coordinate: Coordinate)
}