package net.thechance.mena.identity.presentation.screen.addresses

import net.thechance.mena.identity.presentation.base.BaseInteractionListener
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
@OptIn(ExperimentalUuidApi::class)
interface AddressesScreenInteractionListener : BaseInteractionListener {
    fun onBackButtonClicked()
    fun onAddButtonClicked()
    fun onEditAddressClicked(addressUIState: AddressUIState)
    fun onDeleteAddressClicked(addressId: Uuid)
    fun onConfirmDeleteAddress()
    fun onDismissDeleteDialog()
    fun onDismissSnackBar()
    fun onClickAddress(addressId: Uuid)
}