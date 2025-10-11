package net.thechance.mena.identity.presentation.screen.addresses

sealed class AddressesScreenUIEffect {
     object NavigateBack : AddressesScreenUIEffect()
     data class NavigateToDetailsScreen(val addressId: AddressUIState?) : AddressesScreenUIEffect()
}