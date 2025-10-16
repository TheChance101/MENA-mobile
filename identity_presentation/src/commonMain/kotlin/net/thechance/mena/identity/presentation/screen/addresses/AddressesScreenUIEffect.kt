package net.thechance.mena.identity.presentation.screen.addresses

sealed class AddressesScreenUIEffect {
     object NavigateBack : AddressesScreenUIEffect()
     data class NavigateToAddressDetailsScreen(val addressUIState: AddressUIState?) : AddressesScreenUIEffect()
}