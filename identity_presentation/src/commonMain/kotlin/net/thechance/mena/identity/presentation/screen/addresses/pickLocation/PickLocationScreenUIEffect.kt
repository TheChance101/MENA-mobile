package net.thechance.mena.identity.presentation.screen.addresses.pickLocation

import net.thechance.mena.identity.presentation.screen.addresses.shared.AddressUIState

sealed interface PickLocationScreenUIEffect {
    data object NavigateBack : PickLocationScreenUIEffect
    data class NavigateBackWithLocation(val addressModel: AddressUIState) : PickLocationScreenUIEffect
    data object NavigateToEnableLocation : PickLocationScreenUIEffect
}