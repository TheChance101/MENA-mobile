package net.thechance.mena.faith.presentation.feature.mosque.create

import net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap.CoordinatesUiState

sealed interface CreateMosqueEffect {
    object NavigateBack : CreateMosqueEffect
    data object NavigateToUploadImageRoute : CreateMosqueEffect
    data object NavigateToAddressesScreen : CreateMosqueEffect
    data class NavigateToMap(
        val coordinates: CoordinatesUiState? = null,
    ) : CreateMosqueEffect
}