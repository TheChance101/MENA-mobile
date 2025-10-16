package net.thechance.mena.identity.presentation.screen.addresses

sealed class AddEditLocationScreenUIEffect {
    data object NavigateBack : AddEditLocationScreenUIEffect()
    data class NavigateToMap(
        val locationData: AddLocationScreenUIState.PickLocationData? = null
    ) : AddEditLocationScreenUIEffect()
}