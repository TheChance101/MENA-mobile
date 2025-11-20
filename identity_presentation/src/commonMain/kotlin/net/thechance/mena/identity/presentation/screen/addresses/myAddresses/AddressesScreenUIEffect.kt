package net.thechance.mena.identity.presentation.screen.addresses.myAddresses

import net.thechance.mena.identity.presentation.screen.addresses.shared.AddressUIState

sealed interface AddressesScreenUIEffect {
     object NavigateBack : AddressesScreenUIEffect
     data class NavigateToAddressDetailsScreen(val addressUIState: AddressUIState?, val onSuccess: (SnackBarUiState?)->Unit) : AddressesScreenUIEffect
}