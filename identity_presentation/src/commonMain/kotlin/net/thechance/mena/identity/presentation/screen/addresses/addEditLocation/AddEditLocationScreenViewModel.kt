package net.thechance.mena.identity.presentation.screen.addresses.addEditLocation

import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.add_location_successfully
import mena.identity_presentation.generated.resources.edit_location_successfully
import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.domain.entity.AddressType.AddressTypeMapper.getAddressType
import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.exception.LocationException
import net.thechance.mena.identity.domain.repository.AddressesRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.errorState.ErrorState
import net.thechance.mena.identity.presentation.mapper.createNavigateToMapEffect
import net.thechance.mena.identity.presentation.mapper.mapAuthenticationErrorToMessage
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import net.thechance.mena.identity.presentation.mapper.mapLocationErrorToMessage
import net.thechance.mena.identity.presentation.mapper.toAddressInput
import net.thechance.mena.identity.presentation.screen.addresses.addEditLocation.AddEditLocationScreenUIState.AddEditAddressUIState
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.SnackBarType
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.SnackBarUiState
import net.thechance.mena.identity.presentation.screen.addresses.shared.AddressUIState
import net.thechance.mena.identity.presentation.screen.addresses.shared.CoordinatesUiState
import net.thechance.mena.identity.presentation.screen.addresses.shared.handleLocationAuthenticationException
import net.thechance.mena.identity.presentation.screen.addresses.shared.handleLocationException
import net.thechance.mena.identity.presentation.util.isSaveEnabled
import org.jetbrains.compose.resources.StringResource
import org.maplibre.compose.camera.CameraPosition
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class AddEditLocationScreenViewModel(
    private val addressesRepository: AddressesRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    addressModel: AddressUIState? = null,
) : BaseScreenModel<AddEditLocationScreenUIState, AddEditLocationScreenUIEffect>(
    AddEditLocationScreenUIState()
), AddEditLocationScreenInteractionListener {

    init {
        if (addressModel != null)
            updateAddressState(addressModel, true)
    }

    override fun onClickBack() {
        sendNewEffect(AddEditLocationScreenUIEffect.NavigateBack())
    }

    override fun onClickAddressType(addressType: AddressType) {

        if (addressType == state.value.addressUIState.addressType) return

        updateState {
            copy(
                addressUIState.copy(
                    addressType = addressType,
                    otherAddressType = addressUIState.otherAddressType
                )
            )
        }

        changeIsSaveEnabled()

    }

    override fun onClickSave() {
        updateState { copy(isLoading = true, errorMessage = null) }
        tryToExecute(
            function = ::saveAddress,
            onSuccess = { onSaveAddressSuccess() },
            onError = ::onSaveAddressError,
            dispatcher = dispatcher
        )
    }

    override fun onChangeOtherAddressType(newType: String) {
        updateState {
            copy(
                addressUIState.copy(
                    addressType = AddressType.Other(newType),
                    otherAddressType = newType
                )
            )
        }

        changeIsSaveEnabled()
    }

    override fun onClickMap() {
        sendNewEffect(
            createNavigateToMapEffect(
                addressModel = null,
                onSuccess = ::onAddressFromPickLocation
            )
        )
    }

    override fun onClickEdit() {
        val addressUIState = state.value.addressUIState
        val addressModel = createAddressModelFromCurrentState(addressUIState)
        sendNewEffect(
            createNavigateToMapEffect(
                addressModel = addressModel,
                onSuccess = ::onAddressFromPickLocation
            )
        )
    }

    private suspend fun saveAddress() {
        val addressInput = state.value.addressUIState.toAddressInput()
        val addressId = state.value.addressUIState.addressID
        val isMainAddress = state.value.addressUIState.isMainAddress
        if (addressId != null) {
            addressesRepository.updateAddress(addressId, addressInput, isMainAddress)
        } else {
            addressesRepository.createAddress(addressInput)
        }
    }

    private fun onSaveAddressSuccess() {
        updateState { copy(isLoading = false) }
        val isEditMode = state.value.addressUIState.addressID != null
        val successMessage =
            if (isEditMode) Res.string.edit_location_successfully else Res.string.add_location_successfully
        val snackBarState = SnackBarUiState(
            isVisible = true,
            snackBarType = SnackBarType.SUCCESS,
            message = successMessage
        )
        sendNewEffect(AddEditLocationScreenUIEffect.NavigateBack(snackBarState))
    }

    private fun onAddressFromPickLocation(newAddress: AddressUIState) {
        updateAddressState(newAddress, false)
    }

    private fun onSaveAddressError(throwable: Throwable) {
        val snackBarState = SnackBarUiState(
            isVisible = true,
            snackBarType = SnackBarType.ERROR,
            message = mapErrorMessage(throwable)
        )
        sendNewEffect(AddEditLocationScreenUIEffect.NavigateBack(snackBarState))
    }

    private fun createAddressModelFromCurrentState(addressUIState: AddEditAddressUIState): AddressUIState {
        return AddressUIState(
            id = addressUIState.addressID,
            coordinates = CoordinatesUiState(
                latitude = addressUIState.coordinates.latitude,
                longitude = addressUIState.coordinates.longitude
            ),
            addressType = addressUIState.addressType ?: AddressType.Home,
            addressDetails = addressUIState.addressDetails,
            isMainAddress = addressUIState.isMainAddress
        )
    }

    private fun updateAddressState(newAddress: AddressUIState, updateOriginals: Boolean) {
        updateAddressUIState(newAddress, updateOriginals)
        if (updateOriginals) updateOriginalAddressUIState(newAddress)
        updateMapState(newAddress)
        changeIsSaveEnabled()
    }

    private fun updateAddressUIState(newAddress: AddressUIState, updateOriginals: Boolean) {
        updateState {
            copy(
                addressUIState = addressUIState.copy(
                    coordinates = newAddress.coordinates,
                    addressDetails = newAddress.addressDetails,
                    addressType = if (updateOriginals) newAddress.addressType else addressUIState.addressType,
                    otherAddressType = if (updateOriginals && newAddress.addressType is AddressType.Other)
                        newAddress.addressType.getAddressType()
                    else
                        addressUIState.otherAddressType,
                    addressID = if (updateOriginals)
                        newAddress.id ?: addressUIState.addressID
                    else
                        addressUIState.addressID,
                    isMainAddress = if (updateOriginals) newAddress.isMainAddress else addressUIState.isMainAddress
                )
            )
        }
    }

    private fun updateOriginalAddressUIState(newAddress: AddressUIState) {
        updateState {
            copy(
                originalAddressUIState = originalAddressUIState.copy(
                    coordinates = newAddress.coordinates,
                    addressDetails = newAddress.addressDetails,
                    addressType = newAddress.addressType,
                    otherAddressType = if (newAddress.addressType is AddressType.Other) newAddress.addressType.getAddressType() else null
                )
            )
        }
    }

    private fun updateMapState(newAddress: AddressUIState) {
        updateState {
            copy(
                animateToCurrentLocation = true,
                cameraPosition = CameraPosition(
                    target = Position(
                        latitude = newAddress.coordinates.latitude,
                        longitude = newAddress.coordinates.longitude,
                    ),
                    zoom = 15.0
                )
            )
        }
    }

    private fun changeIsSaveEnabled() {
        val isEnabled = state.value.isSaveEnabled()
        updateState { copy(isSaveEnabled = isEnabled) }
    }

    private fun mapErrorMessage(throwable: Throwable): StringResource {
        return when (throwable) {
            is LocationException -> mapLocationErrorToMessage(handleLocationException(throwable))
            is AuthenticationException -> mapAuthenticationErrorToMessage(
                handleLocationAuthenticationException(throwable)
            )

            else -> mapErrorToMessage(ErrorState.GenericError(throwable))
        }
    }
}
