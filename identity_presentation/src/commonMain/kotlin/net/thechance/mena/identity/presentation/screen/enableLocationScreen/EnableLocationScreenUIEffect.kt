package net.thechance.mena.identity.presentation.screen.enableLocationScreen

sealed interface EnableLocationScreenUIEffect {
    data object NavigateBack : EnableLocationScreenUIEffect
}