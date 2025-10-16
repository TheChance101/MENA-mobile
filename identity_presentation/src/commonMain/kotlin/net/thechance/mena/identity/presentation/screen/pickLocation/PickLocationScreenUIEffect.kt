package net.thechance.mena.identity.presentation.screen.pickLocation

sealed interface PickLocationScreenUIEffect {
    data object NavigateBack : PickLocationScreenUIEffect
    data class NavigateBackWithLocation(val addressModel: AddressModel) : PickLocationScreenUIEffect
    data object NavigateToEnableLocation : PickLocationScreenUIEffect
}