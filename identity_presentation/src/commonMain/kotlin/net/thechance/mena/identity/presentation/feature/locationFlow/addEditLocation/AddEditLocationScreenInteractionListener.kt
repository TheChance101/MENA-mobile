package net.thechance.mena.identity.presentation.feature.locationFlow.addEditLocation

import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.presentation.core.base.BaseInteractionListener

interface AddEditLocationScreenInteractionListener : BaseInteractionListener {
    fun onClickMap()
    fun onClickEdit()
    fun onClickBack()
    fun onClickAddressType(addressType: AddressType)
    fun onClickSave()
    fun onChangeOtherAddressType(newType: String)
}