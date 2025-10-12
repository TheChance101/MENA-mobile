package net.thechance.mena.identity.presentation.screen.addresses

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.repository.AddressesRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.ErrorState
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage

class AddLocationScreenViewModel(
    private val addressesRepository: AddressesRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseScreenModel<AddLocationScreenUIState, AddLocationScreenUIEffect>(
    AddLocationScreenUIState()
), AddLocationScreenInteractionListener {


    private fun onError(errorState: ErrorState) {
        updateState {
            copy(
                isLoading = false, errorMessage = mapErrorToMessage(errorState)
            )
        }
    }

    override fun onClickMap() {
        sendNewEffect(AddLocationScreenUIEffect.NavigateToMap)
    }

    override fun onClickEdit() {
        sendNewEffect(AddLocationScreenUIEffect.NavigateToMap)

    }

    override fun onClickBack() {
        sendNewEffect(AddLocationScreenUIEffect.NavigateBack)

    }

    override fun onClickAddressType(addressType: AddressType) {

        if (addressType == state.value.addressType) return

        updateState { copy(addressType = addressType) }

        changeIsSaveEnabled()

    }

    override fun onClickSave() {
        updateState { copy(isLoading = true, errorMessage = null) }
        tryToExecute(
            function = {
                if (state.value.addressID != null) {
                    addressesRepository.editAddress(
                        addressID = state.value.addressID!!,
                        address = Address(
                            latitude = state.value.latitude,
                            longitude = state.value.longitude,
                            addressLine = state.value.address,
                            addressType = state.value.addressType!!.name,
                            otherAddressType = state.value.otherAddress
                        )
                    )
                } else {
                    addressesRepository.createAddress(
                        address = Address(
                            latitude = state.value.latitude,
                            longitude = state.value.longitude,
                            addressLine = state.value.address,
                            addressType = state.value.addressType!!.name,
                            otherAddressType = state.value.otherAddress
                        )
                    )
                }
            }, onSuccess = ::onSuccess, onError = ::onError, dispatcher = dispatcher
        )
    }

    private fun onSuccess() {
        updateState { copy(isLoading = false) }
        sendNewEffect(AddLocationScreenUIEffect.NavigateBack)
    }

    override fun onChangeAddress(newAddress: String) {
        updateState { copy(address = newAddress) }
        changeIsSaveEnabled()
    }

    override fun onChangeOtherAddressType(newType: String) {
        updateState { copy(otherAddress = newType) }
        changeIsSaveEnabled()
    }

    private fun changeIsSaveEnabled() {
        val isEdit = state.value.addressID != null

        val isEnabled = if (isEdit) {
            val addressChanged = state.value.address != state.value.originalAddress
            val addressTypeChanged = state.value.addressType != state.value.originalAddressType
            val otherAddressChanged = state.value.otherAddress != state.value.originalOtherAddress

            (addressChanged || addressTypeChanged || otherAddressChanged) && (state.value.addressType != AddressType.Other || (state.value.otherAddress?.isNotBlank()
                ?: false))
        } else {
            state.value.address.isNotBlank() && (state.value.addressType != AddressType.Other || (state.value.otherAddress?.isNotBlank()
                ?: false)) && state.value.addressType != null
        }
        updateState { copy(isSaveEnabled = isEnabled) }
    }
}