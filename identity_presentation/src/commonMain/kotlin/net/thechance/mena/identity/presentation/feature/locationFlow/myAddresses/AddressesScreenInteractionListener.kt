package net.thechance.mena.identity.presentation.feature.locationFlow.myAddresses

import net.thechance.mena.identity.presentation.core.base.BaseInteractionListener
import net.thechance.mena.identity.presentation.feature.locationFlow.shared.AddressUIState
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