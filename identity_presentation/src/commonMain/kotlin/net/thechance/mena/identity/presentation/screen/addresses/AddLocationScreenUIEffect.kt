package net.thechance.mena.identity.presentation.screen.addresses

sealed class AddLocationScreenUIEffect {

    data object NavigateBack  : AddLocationScreenUIEffect()
    data object NavigateToMap : AddLocationScreenUIEffect()

}