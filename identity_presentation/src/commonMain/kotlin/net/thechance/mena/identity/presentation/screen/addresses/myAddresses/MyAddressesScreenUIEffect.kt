package net.thechance.mena.identity.presentation.screen.addresses.myAddresses

import net.thechance.mena.identity.presentation.screen.addresses.shared.AddressUIState

sealed interface MyAddressesScreenUIEffect {
     object NavigateBack : MyAddressesScreenUIEffect
     data class NavigateToAddressDetailsScreenMy(
          val addressUIState: AddressUIState?,
          val onSuccess: (SnackBarUiState?) -> Unit
     ) : MyAddressesScreenUIEffect
}