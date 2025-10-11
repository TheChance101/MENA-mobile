package net.thechance.mena.identity.presentation.screen.addresses

import net.thechance.mena.identity.presentation.base.BaseInteractionListener

interface AddressesScreenInteractionListener : BaseInteractionListener {
    fun onBackButtonClicked()
    fun onAddButtonClicked()
    fun onEditAddressClicked(addressId: AddressUIState)
    fun onDeleteAddressClicked(addressId: Long)
    fun onConfirmDeleteAddress()
    fun onDismissDeleteDialog()
    fun onDismissSnackBar()
    fun onAddressClicked(addressId: Long)
    fun clearErrorMessage()
}