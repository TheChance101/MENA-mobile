package net.thechance.mena.identity.presentation.screen.addresses.addEditLocation

import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.SnackBarUiState
import net.thechance.mena.identity.presentation.screen.addresses.shared.AddressUIState

sealed interface AddEditLocationScreenUIEffect {
    data class NavigateBack (val snackBarUiState: SnackBarUiState? = null): AddEditLocationScreenUIEffect
    data class NavigateToMap(val addressModel: AddressUIState? = null, val onUpdateLocation: (AddressUIState) -> Unit) : AddEditLocationScreenUIEffect
}