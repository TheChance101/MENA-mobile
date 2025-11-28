package net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap

import net.thechance.mena.faith.presentation.feature.mosque.MosqueUiState

sealed interface PickLocationScreenUIEffect {
    data object NavigateBack : PickLocationScreenUIEffect
    data class NavigateBackWithLocation(val mosqueLocation: MosqueUiState.Coordinate?) :
        PickLocationScreenUIEffect

    data object NavigateToEnableLocation : PickLocationScreenUIEffect

}