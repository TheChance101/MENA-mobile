package net.thechance.mena.identity.presentation.screen.addresses

sealed class AddEditLocationScreenUIEffect {
    data object NavigateBack : AddEditLocationScreenUIEffect()
    data object NavigateToMap : AddEditLocationScreenUIEffect()
}