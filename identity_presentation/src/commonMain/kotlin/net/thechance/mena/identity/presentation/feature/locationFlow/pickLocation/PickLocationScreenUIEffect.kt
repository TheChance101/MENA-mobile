package net.thechance.mena.identity.presentation.feature.locationFlow.pickLocation

import net.thechance.mena.identity.presentation.feature.locationFlow.shared.AddressUIState

sealed interface PickLocationScreenUIEffect {
    data object NavigateBack : PickLocationScreenUIEffect
    data class NavigateBackWithLocation(val addressModel: AddressUIState) : PickLocationScreenUIEffect
    data object NavigateToEnableLocation : PickLocationScreenUIEffect
}