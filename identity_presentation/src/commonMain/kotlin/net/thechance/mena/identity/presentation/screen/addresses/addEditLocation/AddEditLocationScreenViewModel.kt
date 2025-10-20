package net.thechance.mena.identity.presentation.screen.addresses.addEditLocation

import androidx.compose.ui.unit.DpOffset
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.add_location_successfully
import mena.identity_presentation.generated.resources.edit_location_successfully
import mena.identity_presentation.generated.resources.error
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

        if (addressType !is AddressType.Other) {
            updateState { copy(addressUIState.copy(addressType = addressType, otherAddressType = "" ))}
        }
        else{
            updateState { copy(addressUIState.copy(addressType = AddressType.Other(addressUIState.otherAddressType?:""),
                otherAddressType = addressUIState.otherAddressType ))}
        }
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

    override fun onChangeOtherAddressType(newType: String) {

        updateState { copy(addressUIState.copy(addressType = AddressType.Other(newType), otherAddressType = newType)) }

        changeIsSaveEnabled()
    }

    override fun onClickMap() {
        sendNewEffect(
            AddEditLocationScreenUIEffect.NavigateToMap(
                null, ::updateAddressFromPickLocation
            )
        )
    }

    override fun onSetAnchorLocation(anchorLocation: DpOffset) {
        updateState { copy(anchorLocation = anchorLocation) }
    }

    override fun onClickEdit() {
        val currentState = state.value.addressUIState
        sendNewEffect(
            AddEditLocationScreenUIEffect.NavigateToMap(
                addressModel = AddressUIState(
                    id = currentState.addressID,
                    coordinates = CoordinatesUiState(
                        currentState.coordinates.latitude,
                        currentState.coordinates.longitude
                    ),
                    addressType = currentState.addressType ?: AddressType.Home,
                    addressDetails = currentState.addressDetails,
                    isMainAddress = currentState.isMainAddress
                ), ::updateAddressFromPickLocation
            )
        )
    }

    private suspend fun onSave() {
        if (state.value.addressUIState.addressID != null) {
            addressesRepository.editAddress(state.value.addressUIState.toEntity())
        } else {
            addressesRepository.createAddress(state.value.addressUIState.toEntity())
        }
    }

    private fun onSuccess() {
        updateState { copy(isLoading = false) }
        sendNewEffect(
            AddEditLocationScreenUIEffect.NavigateBack(
                SnackBarUiState(
                    isVisible = true,
                    snackBarType = SnackBarType.SUCCESS,
                    message = if (state.value.addressUIState.addressID != null)
                        Res.string.edit_location_successfully
                    else
                        Res.string.add_location_successfully
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
                    message = Res.string.error
                )
            )
        )

    }

    private fun updateAddressFromPickLocation(newAddress: AddressUIState) {
        updateAddressState(newAddress, false)
    }

    private fun updateAddressState(newAddress: AddressUIState, updateOriginals: Boolean) {
        updateState {
            copy(
                addressUIState = addressUIState.copy(
                    coordinates = newAddress.coordinates,
                    addressDetails = newAddress.addressDetails,
                    addressType = if (addressUIState.addressType != null || updateOriginals) newAddress.addressType else null,
                    otherAddressType = if (newAddress.addressType is AddressType.Other) newAddress.addressType.getAddressType() else null,
                    addressID = newAddress.id,
                    isMainAddress = newAddress.isMainAddress
                ),

                originalAddressUIState = if (updateOriginals) originalAddressUIState.copy(
                    coordinates = newAddress.coordinates,
                    addressDetails = newAddress.addressDetails,
                    addressType = newAddress.addressType,
                    otherAddressType = if (newAddress.addressType is AddressType.Other) newAddress.addressType.getAddressType() else null
                ) else this.originalAddressUIState,

                animateToCurrentLocation = true,
                cameraPosition = CameraPosition(
                    target = Position(
                        latitude = newAddress.coordinates.latitude,
                        longitude = newAddress.coordinates.longitude,
                    ),
                    zoom = 15.0
                ),

                )
        }
        changeIsSaveEnabled()
    }

    private fun changeIsSaveEnabled() {

        val currentState = state.value
        val isEditMode = currentState.addressUIState.addressID != null

        val isEnabled = if (isEditMode) {
            hasAddressChanged(currentState) && isAddressInputValid(currentState)
        } else {
            currentState.addressUIState.addressDetails.isNotBlank() && isAddressInputValid(
                currentState
            )
        }

        updateState { copy(isSaveEnabled = isEnabled) }
    }

    private fun isAddressInputValid(currentState: AddEditLocationScreenUIState): Boolean {
        return when (currentState.addressUIState.addressType) {
            AddressType.Home -> true
            AddressType.Office -> true
            is AddressType.Other -> !currentState.addressUIState.otherAddressType.isNullOrBlank()
            else -> false
        }
    }

    private fun hasAddressChanged(currentState: AddEditLocationScreenUIState): Boolean {
        return currentState.addressUIState.addressDetails != currentState.originalAddressUIState.addressDetails ||
                currentState.addressUIState.addressType != currentState.originalAddressUIState.addressType ||
                currentState.addressUIState.otherAddressType != currentState.originalAddressUIState.otherAddressType ||
                currentState.addressUIState.coordinates != currentState.originalAddressUIState.coordinates
    }

}