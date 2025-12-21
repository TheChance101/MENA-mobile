package net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap


internal sealed interface PickLocationScreenUIEffect {
    data object NavigateBack : PickLocationScreenUIEffect
    data class NavigateBackWithLocation(val mosqueLocation: AddressModel) :
        PickLocationScreenUIEffect
}