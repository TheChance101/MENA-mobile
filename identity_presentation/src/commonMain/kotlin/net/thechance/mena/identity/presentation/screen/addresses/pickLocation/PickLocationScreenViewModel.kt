package net.thechance.mena.identity.presentation.screen.addresses.pickLocation

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error_location_is_turned_off
import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.domain.exception.LocationException
import net.thechance.mena.identity.domain.model.Coordinates
import net.thechance.mena.identity.domain.repository.AddressesRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.errorState.ErrorState
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import net.thechance.mena.identity.presentation.mapper.mapLocationErrorToMessage
import net.thechance.mena.identity.presentation.screen.addresses.shared.AddressUIState
import net.thechance.mena.identity.presentation.screen.addresses.shared.CoordinatesUiState
import net.thechance.mena.identity.presentation.screen.addresses.shared.handleLocationException
import net.thechance.mena.identity.presentation.screen.addresses.shared.toEntity
import net.thechance.mena.identity.presentation.screen.addresses.shared.toUiState
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionHandler
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi

class PickLocationScreenViewModel(
    private val addressesRepository: AddressesRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val locationForegroundHandler: PermissionHandler,
    private val addressModel: AddressUIState? = null,
) : BaseScreenModel<PickLocationScreenUIState, PickLocationScreenUIEffect>(PickLocationScreenUIState()),
    PickLocationScreenInteractionListener {

    init {
        if (addressModel != null)
            updateAddress(addressModel)
    }

    fun updateAddress(addressModel: AddressUIState?) {
        if (addressModel != null) {
            updateState {
                copy(
                    currentLocation = CoordinatesUiState(
                        latitude = addressModel.coordinates.latitude,
                        longitude = addressModel.coordinates.longitude
                    ),
                    showAnchor = true,
                    address = addressModel.addressDetails,
                    animateToCurrentLocation = true,
                    isMainAddress = addressModel.isMainAddress
                )
            }
        }
    }

    override fun onClickMap(coordinates: CoordinatesUiState) {
        updateState { copy(currentLocation = coordinates, showAnchor = true) }
        getLocationName()
    }

    private fun getLocationName() {
        tryToExecute(
            function = ::fetchLocationName,
            onSuccess = ::onLocationNameSuccess,
            onError = ::onLocationNameError,
            dispatcher = Dispatchers.Main
        )
    }

    private suspend fun fetchLocationName(): String {
        return addressesRepository.getLocationName(state.value.currentLocation.toEntity())
    }

    private fun onLocationNameSuccess(address: String) {
        updateState { copy(address = address) }
        changeIsConfirmEnabled()
    }

    private fun onLocationNameError(throwable: Throwable) {
        updateState { copy(isGpsButtonLoading = false, address = "") }
        sendNewEffect(
            PickLocationScreenUIEffect.ShowSnackBarError(
                errorStringResource = mapErrorMessage(throwable)
            )
        )
        changeIsConfirmEnabled()
    }

    override fun onMoveCamera(
        coordinates: CoordinatesUiState
    ) {
        updateState {
            copy(
                currentLocation = coordinates,
                animateToCurrentLocation = false,
                showAnchor = true
            )
        }
        getLocationName()
    }

    override fun onClickGps() {
        tryToExecute(
            function = ::fetchCurrentLocation,
            onSuccess = ::onCurrentLocationSuccess,
            onError = ::onCurrentLocationError,
            dispatcher = Dispatchers.Main
        )
    }

    private suspend fun fetchCurrentLocation(): Coordinates? {
        updateState { copy(isGpsButtonLoading = true) }
        return addressesRepository.getCurrentLocation()
    }

    private fun onCurrentLocationSuccess(coordinates: Coordinates?) {
        if (coordinates != null) {
            updateState {
                copy(
                    currentLocation = coordinates.toUiState(),
                    animateToCurrentLocation = true,
                    isGpsButtonLoading = false,
                    showAnchor = true
                )
            }
            getLocationName()
        }
    }

    private fun onCurrentLocationError(throwable: Throwable) {
        checkLocationEnable()
    }

    private fun checkLocationEnable() {
        tryToExecute(
            function = { locationForegroundHandler.checkPermission() },
            onSuccess = ::onPermissionCheckSuccess,
            onError = ::onPermissionCheckError,
            dispatcher = dispatcher
        )
    }

    private fun onPermissionCheckSuccess(permissionState: PermissionState) {
        when (permissionState) {
            PermissionState.GRANTED -> {
                updateState {
                    copy(
                        isGpsButtonLoading = false
                    )
                }
                sendNewEffect(
                    PickLocationScreenUIEffect.ShowSnackBarError(
                        errorStringResource = Res.string.error_location_is_turned_off
                    )
                )
            }

            PermissionState.DENIED -> {
                navigateToEnableLocation()
            }

            PermissionState.NOT_DETERMINED -> {
                navigateToEnableLocation()
            }

            PermissionState.DENIED_PERMANENTLY -> {
                navigateToEnableLocation()
            }
        }
    }

    private fun onPermissionCheckError(throwable: Throwable) {
        updateState {
            copy(
                isGpsButtonLoading = false,
                address = "",
            )
        }
        sendNewEffect(
            PickLocationScreenUIEffect.ShowSnackBarError(
                errorStringResource = mapErrorMessage(throwable)
            )
        )
        changeIsConfirmEnabled()
    }

    private fun navigateToEnableLocation() {
        sendNewEffect(PickLocationScreenUIEffect.NavigateToEnableLocation)
        updateState { copy(isGpsButtonLoading = false) }
    }


    @OptIn(ExperimentalUuidApi::class)
    override fun onClickConfirm() {
        sendNewEffect(
            PickLocationScreenUIEffect.NavigateBackWithLocation(
                AddressUIState(
                    id = addressModel?.id,
                    coordinates = CoordinatesUiState(
                        state.value.currentLocation.latitude,
                        state.value.currentLocation.longitude
                    ),
                    addressType = addressModel?.addressType ?: AddressType.Home,
                    addressDetails = state.value.address,
                    isMainAddress = state.value.isMainAddress
                )
            )
        )
    }

    override fun onClickBack() {
        sendNewEffect(PickLocationScreenUIEffect.NavigateBack)
    }

    private fun changeIsConfirmEnabled() {
        if (state.value.address.isNotBlank()) {
            updateState { copy(isConfirmEnabled = true) }
        } else {
            updateState { copy(isConfirmEnabled = false) }
        }
    }

    private fun mapErrorMessage(throwable: Throwable): StringResource {
        return when (throwable) {
            is LocationException -> mapLocationErrorToMessage(handleLocationException(throwable))
            else -> mapErrorToMessage(ErrorState.GenericError(throwable))
        }
    }
}