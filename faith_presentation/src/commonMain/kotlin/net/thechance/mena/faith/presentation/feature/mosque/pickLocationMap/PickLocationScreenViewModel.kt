package net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.domain.exception.LocationException
import net.thechance.mena.identity.domain.model.Coordinates
import net.thechance.mena.identity.domain.repository.AddressesRepository
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi

class PickLocationScreenViewModel(
    private val addressesRepository: AddressesRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val locationForegroundHandler: PermissionHandler,
) : BaseViewModel<PickLocationScreenUIState, PickLocationScreenUIEffect>(PickLocationScreenUIState()),
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
        sendEffect(
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
            execute = { locationForegroundHandler.checkPermission() },
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
                sendEffect(
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
        sendEffect(
            PickLocationScreenUIEffect.ShowSnackBarError(
                errorStringResource = mapErrorMessage(throwable)
            )
        )
        changeIsConfirmEnabled()
    }

    private fun navigateToEnableLocation() {
        sendEffect(PickLocationScreenUIEffect.NavigateToEnableLocation)
        updateState { copy(isGpsButtonLoading = false) }
    }


    @OptIn(ExperimentalUuidApi::class)
    override fun onClickConfirm() {
        sendEffect(
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
        sendEffect(PickLocationScreenUIEffect.NavigateBack)
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