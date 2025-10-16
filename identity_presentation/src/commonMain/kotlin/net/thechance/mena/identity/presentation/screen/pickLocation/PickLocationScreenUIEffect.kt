package net.thechance.mena.identity.presentation.screen.pickLocation

sealed interface PickLocationScreenUIEffect {
    data object NavigateBack : PickLocationScreenUIEffect
    data class NavigateToAddLocation(
        val latitude: Double? = null,
        val longitude: Double? = null,
        val address: String? = null
    ) : PickLocationScreenUIEffect

    data object NavigateToEnableLocation : PickLocationScreenUIEffect
}