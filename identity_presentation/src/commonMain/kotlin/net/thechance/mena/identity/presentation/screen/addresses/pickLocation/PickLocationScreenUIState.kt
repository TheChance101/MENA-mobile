package net.thechance.mena.identity.presentation.screen.addresses.pickLocation

import net.thechance.mena.identity.presentation.screen.addresses.shared.CoordinatesUiState
import org.jetbrains.compose.resources.StringResource

data class PickLocationScreenUIState(
    val currentLocation: CoordinatesUiState = CoordinatesUiState(),
    val animateToCurrentLocation: Boolean = false,
    val showAnchor: Boolean = false,
    val address: String = "",
    val isLoading: Boolean = false,
    val errorMessage: StringResource? = null,
    val isConfirmEnabled: Boolean = false,
    val isGpsButtonLoading: Boolean = false,
    val isMainAddress: Boolean = false
)
