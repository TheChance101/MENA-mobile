package net.thechance.mena.identity.presentation.screen.addresses

sealed class AddEditLocationScreenUIEffect {
    data object NavigateBack : AddEditLocationScreenUIEffect()
    data class NavigateToMap(
        val latitude: Double? = null,
        val longitude: Double? = null,
        val address: String? = null
    ) : AddEditLocationScreenUIEffect()
}