package net.thechance.mena.identity.presentation.screen.addresses

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.domain.repository.AddressesRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.ErrorState
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
@OptIn(ExperimentalUuidApi::class)

class AddEditLocationScreenViewModel(
    private val addressesRepository: AddressesRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseScreenModel<AddLocationScreenUIState, AddEditLocationScreenUIEffect>
    (AddLocationScreenUIState()),
    AddEditLocationScreenInteractionListener {

    override fun onClickMap() {

        sendNewEffect(AddEditLocationScreenUIEffect.NavigateToMap)
    }

    override fun onClickEdit() {

        sendNewEffect(AddEditLocationScreenUIEffect.NavigateToMap)
    }

    override fun onClickBack() {

        sendNewEffect(AddEditLocationScreenUIEffect.NavigateBack)
    }

    override fun onClickAddressType(addressType: AddressType) {

        if (addressType == state.value.addressType) return

        updateState { copy(addressType = addressType) }

        changeIsSaveEnabled()

    }

    override fun onClickSave() {

        updateState { copy(isLoading = true, errorMessage = null) }

        tryToExecute(
            function = ::onSave,
            onSuccess = ::onSuccess,
            onError = ::onError,
            dispatcher = dispatcher
        )
    }

    override fun onChangeAddress(newAddress: String) {

        updateState { copy(address = newAddress) }
        changeIsSaveEnabled()
    }

    override fun onChangeOtherAddressType(newType: String) {

        updateState { copy(otherAddress = newType) }
        changeIsSaveEnabled()
    }

    fun setInitialAddressData(
        addressID: String,
        address: String,
        latitude: Double,
        longitude: Double,
        addressType: AddressType,
        otherAddress: String?,
        isActive: Boolean
    ) {
        updateState {
            copy(
                addressID = addressID,
                address = address,
                originalAddress = address,
                latitude = latitude,
                longitude = longitude,
                addressType = addressType,
                originalAddressType = addressType,
                otherAddress = otherAddress,
                originalOtherAddress = otherAddress,
                isActive = isActive
            )
        }
    }

    private suspend fun onSave() {
        if (state.value.addressID != null) {
            addressesRepository.editAddress(
                address = Address(
                    id =Uuid.parse(state.value.addressID!!),
                    latitude = state.value.latitude,
                    longitude = state.value.longitude,
                    addressLine = state.value.address,
                    addressType = state.value.addressType!!.name,
                    otherAddressType = state.value.otherAddress,
                    isActive = state.value.isActive
                )
            )
        } else {
            addressesRepository.createAddress(
                address = Address(
                    latitude = state.value.latitude,
                    longitude = state.value.longitude,
                    addressLine = state.value.address,
                    addressType = state.value.addressType?.name ?: "",
                    otherAddressType = state.value.otherAddress,
                    isActive = state.value.isActive
                )
            )
        }
    }

    private fun onSuccess() {
        updateState { copy(isLoading = false) }
        sendNewEffect(AddEditLocationScreenUIEffect.NavigateBack)
    }

    private fun onError(errorState: ErrorState) {
        updateState {
            copy(
                isLoading = false,
                errorMessage = mapErrorToMessage(errorState)
            )
        }
    }

    private fun changeIsSaveEnabled() {
        val isEdit = state.value.addressID != null

        val isEnabled = if (isEdit) {
            val addressChanged = state.value.address != state.value.originalAddress
            val addressTypeChanged = state.value.addressType != state.value.originalAddressType
            val otherAddressChanged = state.value.otherAddress != state.value.originalOtherAddress

            (addressChanged || addressTypeChanged || otherAddressChanged)
                    && (state.value.addressType != AddressType.Other || (state.value.otherAddress?.isNotBlank()
                ?: false))

        } else {

            state.value.address.isNotBlank()
                    && (state.value.addressType != AddressType.Other || (state.value.otherAddress?.isNotBlank()
                ?: false))
                    && state.value.addressType != null
        }
        updateState { copy(isSaveEnabled = isEnabled) }
    }
}