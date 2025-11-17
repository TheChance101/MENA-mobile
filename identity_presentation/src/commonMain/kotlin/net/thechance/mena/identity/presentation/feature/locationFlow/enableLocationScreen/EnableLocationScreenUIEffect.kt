package net.thechance.mena.identity.presentation.feature.locationFlow.enableLocationScreen

sealed interface EnableLocationScreenUIEffect {
    data object NavigateBack : EnableLocationScreenUIEffect
}