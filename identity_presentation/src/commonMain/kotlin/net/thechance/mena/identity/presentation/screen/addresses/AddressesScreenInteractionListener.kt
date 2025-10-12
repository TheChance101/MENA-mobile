package net.thechance.mena.identity.presentation.screen.addresses

import net.thechance.mena.identity.presentation.base.BaseInteractionListener
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
@OptIn(ExperimentalUuidApi::class)
interface AddressesScreenInteractionListener : BaseInteractionListener {
    fun onBackButtonClicked()
    fun onAddButtonClicked()
    fun onEditAddressClicked(addressId: AddressUIState)

    fun onDeleteAddressClicked(addressId: Uuid)
    fun onConfirmDeleteAddress()
    fun onDismissDeleteDialog()
    fun onDismissSnackBar()
    fun onAddressClicked(addressId: Uuid)
    fun clearErrorMessage()
}