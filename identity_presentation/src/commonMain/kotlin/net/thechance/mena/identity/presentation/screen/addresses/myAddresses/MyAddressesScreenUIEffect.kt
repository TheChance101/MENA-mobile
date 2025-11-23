package net.thechance.mena.identity.presentation.screen.addresses.myAddresses

import net.thechance.mena.identity.presentation.screen.addresses.shared.AddressUIState
import org.jetbrains.compose.resources.StringResource

sealed interface MyAddressesScreenUIEffect {
    object NavigateBack : MyAddressesScreenUIEffect
    data class NavigateToAddressDetailsScreen(
        val addressUIState: AddressUIState?,
    ) : MyAddressesScreenUIEffect

    data class ShowSnackBarError(val errorStringResource: StringResource) :
        MyAddressesScreenUIEffect

    data class ShowSnackBarSuccess(val successStringResource: StringResource) :
        MyAddressesScreenUIEffect

}