package net.thechance.mena.identity.presentation.screen.addresses

import net.thechance.mena.identity.presentation.screen.addresses.pickLocation.AddressModel

sealed class AddEditLocationScreenUIEffect {
    data object NavigateBack : AddEditLocationScreenUIEffect()
    data class NavigateToMap(val addressModel: AddressModel? = null, val onUpdateLocation: (AddressModel) -> Unit) : AddEditLocationScreenUIEffect()
}