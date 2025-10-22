package net.thechance.mena.identity.presentation.screen.addresses.myAddresses

sealed class AddressesScreenUIEffect {
     object NavigateBack : AddressesScreenUIEffect()
     data class NavigateToAddressDetailsScreen(val addressUIState: AddressUIState?, val onSuccess: (SnackBarUiState?)->Unit) : AddressesScreenUIEffect()
}