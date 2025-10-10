package net.thechance.mena.identity.presentation.screen.pickLocation

sealed interface PickLocationScreenUIEffect {
    data object NavigateBack : PickLocationScreenUIEffect
    data class NavigateToAddLocation(
        val latitude: Double,
        val longitude: Double,
        val address: String
    ) : PickLocationScreenUIEffect
}