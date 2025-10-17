package net.thechance.mena.identity.presentation.screen.addresses.pickLocation

sealed interface PickLocationScreenUIEffect {
    data object NavigateBack : PickLocationScreenUIEffect
    data class NavigateBackWithLocation(val addressModel: AddressModel) : PickLocationScreenUIEffect
    data object NavigateToEnableLocation : PickLocationScreenUIEffect
}