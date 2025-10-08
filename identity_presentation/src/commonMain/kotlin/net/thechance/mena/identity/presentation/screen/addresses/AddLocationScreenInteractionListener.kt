package net.thechance.mena.identity.presentation.screen.addresses

interface AddLocationScreenInteractionListener {

    fun onClickMap()
    fun onClickEdit()
    fun onClickBack()
    fun onClickAddressType()
    fun onClickSave()
    fun onAddressChanged(newAddress:String)
    fun onOtherAddressTypeChanged(newType:String)

}