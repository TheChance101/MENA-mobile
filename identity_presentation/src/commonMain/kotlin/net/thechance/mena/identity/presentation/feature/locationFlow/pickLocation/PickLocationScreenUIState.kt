package net.thechance.mena.identity.presentation.feature.locationFlow.pickLocation

import net.thechance.mena.identity.presentation.feature.locationFlow.shared.CoordinatesUiState
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
