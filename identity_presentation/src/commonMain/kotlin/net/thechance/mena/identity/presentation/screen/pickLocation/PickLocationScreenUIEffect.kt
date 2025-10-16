package net.thechance.mena.identity.presentation.screen.pickLocation

import net.thechance.mena.identity.presentation.screen.addresses.AddLocationScreenUIState

sealed interface PickLocationScreenUIEffect {
    data object NavigateBack : PickLocationScreenUIEffect
    data class NavigateToAddLocation(
        val locationData: AddLocationScreenUIState.PickLocationData? = null
    ) : PickLocationScreenUIEffect

    data object NavigateToEnableLocation : PickLocationScreenUIEffect
}