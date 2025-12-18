package net.thechance.mena.faith.presentation.navigation

import net.thechance.mena.faith.presentation.feature.mosque.create.CreateMosqueEffect
import net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap.CoordinatesUiState

fun createNavigateToMapEffect(
    coordinates: CoordinatesUiState? = null
): CreateMosqueEffect {
    return CreateMosqueEffect.NavigateToMap(coordinates)
}