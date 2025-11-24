package net.thechance.mena.faith.presentation.feature.mosque

internal sealed interface NearbyMosquesEffect {
    data object NavigateToAddressesScreen : NearbyMosquesEffect
    data object NavigateToAddMosque : NearbyMosquesEffect
    data object NavigateBack : NearbyMosquesEffect
    data class NavigateToMap(val coordinate: MosqueUiState.Coordinate) : NearbyMosquesEffect
}
