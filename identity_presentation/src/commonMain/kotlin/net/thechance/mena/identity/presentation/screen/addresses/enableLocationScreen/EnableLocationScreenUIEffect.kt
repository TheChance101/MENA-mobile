package net.thechance.mena.identity.presentation.screen.addresses.enableLocationScreen

sealed interface EnableLocationScreenUIEffect {
    data object NavigateBack : EnableLocationScreenUIEffect
}