package net.thechance.mena.identity.presentation.feature.locationFlow.myAddresses

import net.thechance.mena.identity.presentation.feature.locationFlow.shared.AddressUIState

sealed interface AddressesScreenUIEffect {
     object NavigateBack : AddressesScreenUIEffect
     data class NavigateToAddressDetailsScreen(val addressUIState: AddressUIState?, val onSuccess: (SnackBarUiState?)->Unit) : AddressesScreenUIEffect
}