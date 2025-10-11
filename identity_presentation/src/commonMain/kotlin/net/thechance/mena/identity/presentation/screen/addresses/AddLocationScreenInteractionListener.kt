package net.thechance.mena.identity.presentation.screen.addresses

import net.thechance.mena.identity.presentation.base.BaseInteractionListener

interface AddLocationScreenInteractionListener: BaseInteractionListener{

    fun onClickMap()
    fun onClickEdit()
    fun onClickBack()
    fun onClickAddressType(addressType: AddressType)
    fun onClickSave()
    fun onAddressChanged(newAddress:String)
    fun onOtherAddressTypeChanged(newType:String)

}