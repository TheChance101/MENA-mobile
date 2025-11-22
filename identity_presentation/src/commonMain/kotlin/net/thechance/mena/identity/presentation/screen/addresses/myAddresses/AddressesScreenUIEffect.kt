package net.thechance.mena.identity.presentation.screen.addresses.myAddresses

import net.thechance.mena.identity.presentation.screen.addresses.shared.AddressUIState
import org.jetbrains.compose.resources.StringResource

sealed interface AddressesScreenUIEffect {
    object NavigateBack : AddressesScreenUIEffect
    data class NavigateToAddressDetailsScreen(
        val addressUIState: AddressUIState?,
    ) : AddressesScreenUIEffect

    data class ShowSnackBarError(val errorStringResource: StringResource) : AddressesScreenUIEffect

    data class ShowSnackBarSuccess(val successStringResource: StringResource) :
        AddressesScreenUIEffect

}