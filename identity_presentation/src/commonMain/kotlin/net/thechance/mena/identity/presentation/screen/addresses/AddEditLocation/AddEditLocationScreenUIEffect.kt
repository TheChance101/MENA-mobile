package net.thechance.mena.identity.presentation.screen.addresses.AddEditLocation

sealed class AddEditLocationScreenUIEffect {
    data object NavigateBack : AddEditLocationScreenUIEffect()
    data object NavigateToMap : AddEditLocationScreenUIEffect()
}