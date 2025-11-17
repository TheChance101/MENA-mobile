package net.thechance.mena.identity.presentation.feature.locationFlow.addEditLocation

import net.thechance.mena.identity.presentation.feature.locationFlow.myAddresses.SnackBarUiState
import net.thechance.mena.identity.presentation.feature.locationFlow.shared.AddressUIState

sealed interface AddEditLocationScreenUIEffect {
    data class NavigateBack(val snackBarUiState: SnackBarUiState? = null) :
        AddEditLocationScreenUIEffect

    data class NavigateToMap(
        val addressModel: AddressUIState? = null,
        val onUpdateLocation: (AddressUIState) -> Unit
    ) :
        AddEditLocationScreenUIEffect
}