package net.thechance.mena.identity.presentation.screen.addresses.addEditLocation

import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.presentation.base.BaseInteractionListener

interface AddEditLocationScreenInteractionListener : BaseInteractionListener {
    fun onClickMap()
    fun onClickEdit()
    fun onClickBack()
    fun onClickAddressType(addressType: AddressType)
    fun onClickSave()
    fun onChangeOtherAddressType(newType: String)
}