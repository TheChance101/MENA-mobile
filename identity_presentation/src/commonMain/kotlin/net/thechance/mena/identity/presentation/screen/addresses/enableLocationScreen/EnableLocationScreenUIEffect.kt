package net.thechance.mena.identity.presentation.screen.addresses.enableLocationScreen

import org.jetbrains.compose.resources.StringResource

sealed interface EnableLocationScreenUIEffect {
    data object NavigateBack : EnableLocationScreenUIEffect

    data class ShowSnackBarError(val errorStringResource: StringResource) :
        EnableLocationScreenUIEffect
}