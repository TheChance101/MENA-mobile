package net.thechance.mena.identity.presentation.screen.addresses.AddEditLocation

import androidx.compose.ui.unit.DpOffset
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.add_location_successfully
import mena.identity_presentation.generated.resources.edit_location_successfully
import mena.identity_presentation.generated.resources.is_main_address_error
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.domain.entity.AddressType.AddressTypeMapper.getAddressType
import net.thechance.mena.identity.domain.repository.AddressesRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.ErrorState
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import net.thechance.mena.identity.presentation.screen.addresses.AddressUIState
import net.thechance.mena.identity.presentation.screen.addresses.CoordinatesUiState
import net.thechance.mena.identity.presentation.screen.addresses.SnackBarType
import net.thechance.mena.identity.presentation.screen.addresses.SnackBarUiState
import org.maplibre.compose.camera.CameraPosition
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class AddEditLocationScreenViewModel(
    private val addressesRepository: AddressesRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val addressModel: AddressUIState? = null,
) : BaseScreenModel<AddLocationScreenUIState, AddEditLocationScreenUIEffect>(
    AddLocationScreenUIState()
), AddEditLocationScreenInteractionListener {
    init {
        if (addressModel != null)
            updateAddress(addressModel)
    }

    override fun onClickBack() {
        sendNewEffect(AddEditLocationScreenUIEffect.NavigateBack())
    }

    override fun onClickAddressType(addressType: AddressType) {

        if (addressType == state.value.addressType) return
        if(addressType == AddressType.Home || addressType == AddressType.Office) {
            updateState { copy(otherAddress = null) }
        }else{
            updateState { copy(otherAddress = "") }
        }

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

    override fun onSetAnchorLocation(anchorLocation: DpOffset) {
        updateState { copy(anchorLocation = anchorLocation) }
    }

    private suspend fun onSave() {
        if (state.value.addressID != null) {
            addressesRepository.editAddress(
                address = Address(
                    id = Uuid.parse(state.value.addressID!!),
                    latitude = state.value.latitude,
                    longitude = state.value.longitude,
                    addressLine = state.value.address,
                    addressType =
                        if (!state.value.otherAddress.isNullOrBlank())
                            AddressType.Other(state.value.otherAddress!!)
                        else
                            state.value.addressType!!,

                    isActive = state.value.isActive
                )
            )
        } else {
            addressesRepository.createAddress(
                address = Address(
                    latitude = state.value.latitude,
                    longitude = state.value.longitude,
                    addressLine = state.value.address,
                    addressType =
                        if (!state.value.otherAddress.isNullOrBlank())
                            AddressType.Other(state.value.otherAddress!!)
                        else
                            state.value.addressType!!,

                    isActive = state.value.isActive
                )
            )
        }
    }

    private fun onSuccess() {
        updateState { copy(isLoading = false) }
        sendNewEffect(
            AddEditLocationScreenUIEffect.NavigateBack(
                SnackBarUiState(
                    isVisible = true,
                    snackBarType = SnackBarType.SUCCESS,
                    message = if (state.value.addressID != null) Res.string.edit_location_successfully else Res.string.add_location_successfully
                )
            )
        )
    }

    private fun onError(errorState: ErrorState) {
        updateState {
            copy(
                isLoading = false, errorMessage = mapErrorToMessage(errorState)
            )
        }
        sendNewEffect(
            AddEditLocationScreenUIEffect.NavigateBack(
                SnackBarUiState(
                    isVisible = true,
                    snackBarType = SnackBarType.ERROR,
                    message = Res.string.is_main_address_error
                )
            )
        )

    }

    override fun onClickEdit() {
        sendNewEffect(
            AddEditLocationScreenUIEffect.NavigateToMap(
                addressModel = AddressUIState(
                    id = state.value.addressID?.let { Uuid.parse(it) },
                    coordinates = CoordinatesUiState(
                        state.value.latitude,
                        state.value.longitude
                    ),
                    addressDetails = state.value.address,
                    isMainAddress = state.value.isActive
                ), ::updateAddressFromPickLocation
            )
        )
    }

    override fun onClickMap() {
        sendNewEffect(
            AddEditLocationScreenUIEffect.NavigateToMap(
                null, ::updateAddressFromPickLocation
            )
        )
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
                originalOtherAddress = otherAddress,
                isActive = isActive
            )
        }
    }

    private fun updateAddressFromPickLocation(newAddress: AddressUIState) {
        updateState {
            copy(
                latitude = newAddress.coordinates.latitude,
                longitude = newAddress.coordinates.longitude,
                address = newAddress.addressDetails,
                addressID = newAddress.id?.toString(),
                animateToCurrentLocation = true,
                cameraPosition = CameraPosition(
                    target = Position(
                        latitude = newAddress.coordinates.latitude,
                        longitude = newAddress.coordinates.longitude,
                    ),
                    zoom = 15.0
                ),
                isActive = newAddress.isMainAddress
            )
        }
        changeIsSaveEnabled()
    }

    private fun updateAddress(newAddress: AddressUIState) {
        updateState {
            copy(
                originalLatitude = newAddress.coordinates.latitude,
                latitude = newAddress.coordinates.latitude,
                originalLongitude = newAddress.coordinates.longitude,
                longitude = newAddress.coordinates.longitude,
                originalAddress = newAddress.addressDetails,
                address = newAddress.addressDetails,
                originalAddressType = newAddress.addressType,
                addressType = newAddress.addressType,
                originalOtherAddress = if(newAddress.addressType is AddressType.Other) newAddress.addressType.getAddressType() else null,
                addressID = newAddress.id?.toString(),
                otherAddress = if(newAddress.addressType is AddressType.Other) newAddress.addressType.getAddressType() else null,
                animateToCurrentLocation = true,
                cameraPosition = CameraPosition(
                    target = Position(
                        latitude = newAddress.coordinates.latitude,
                        longitude = newAddress.coordinates.longitude,
                    ),
                    zoom = 15.0
                ),
                isActive = newAddress.isMainAddress
            )
        }
    }

    private fun changeIsSaveEnabled() {
        val isEdit = state.value.addressID != null

        val isEnabled = if (isEdit) {
            val addressChanged = state.value.address != state.value.originalAddress
            val addressTypeChanged = state.value.addressType != state.value.originalAddressType
            val otherAddressChanged = state.value.otherAddress != state.value.originalOtherAddress
            val longitudeChanged = state.value.longitude != state.value.originalLongitude
            val latitudeChanged = state.value.latitude != state.value.originalLatitude

            (addressChanged || addressTypeChanged || otherAddressChanged || longitudeChanged || latitudeChanged)
                    && (state.value.addressType != AddressType.Other("") || (state.value.otherAddress?.isNotBlank()
                ?: false))

        } else {

            state.value.address.isNotBlank()
                    && (state.value.addressType != AddressType.Other("") || (state.value.otherAddress?.isNotBlank()
                ?: false))
                    && state.value.addressType != null
        }
        updateState { copy(isSaveEnabled = isEnabled) }
    }
}