package net.thechance.mena.faith.presentation.feature.mosque

internal sealed interface MosqueMapEffect {
    data object NavigateToUserLocation : MosqueMapEffect
    data object NavigateToAddMosque : MosqueMapEffect
    data object NavigateBack : MosqueMapEffect
}