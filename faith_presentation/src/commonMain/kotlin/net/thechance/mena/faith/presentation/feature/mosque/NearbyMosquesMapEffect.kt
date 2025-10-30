package net.thechance.mena.faith.presentation.feature.mosque

internal sealed interface NearbyMosquesMapEffect {
    data object NavigateToUserLocation : NearbyMosquesMapEffect
    data object NavigateToAddMosque : NearbyMosquesMapEffect
    data object NavigateBack : NearbyMosquesMapEffect
}