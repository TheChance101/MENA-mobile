package net.thechance.mena.identity.presentation.screen.addresses.myAddresses

import net.thechance.mena.identity.presentation.screen.addresses.AddressUIState

sealed class AddressesScreenUIEffect {
     object NavigateBack : AddressesScreenUIEffect()
     data class NavigateToAddressDetailsScreen(val addressUIState: AddressUIState?, val onSuccess: ()->Unit) : AddressesScreenUIEffect()
}