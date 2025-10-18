package net.thechance.mena.identity.presentation.screen.addresses.pickLocation

import androidx.compose.ui.unit.DpOffset
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.domain.repository.MobileLocationRepository
import net.thechance.mena.identity.domain.util.Coordinates
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.ErrorState
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import net.thechance.mena.identity.presentation.screen.addresses.AddressUIState
import net.thechance.mena.identity.presentation.screen.addresses.CoordinatesUiState
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionHandler
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState
import org.maplibre.compose.camera.CameraPosition
import kotlin.uuid.ExperimentalUuidApi

class PickLocationScreenViewModel(
    private val mobileLocationRepository: MobileLocationRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val locationForegroundHandler: PermissionHandler,
    private val addressModel: AddressUIState?,
) : BaseScreenModel<PickLocationScreenUIState, PickLocationScreenUIEffect>(PickLocationScreenUIState()),
    PickLocationScreenInteractionListener {

    init {
        onUpdateAddress(addressModel)
    }

    override fun onUpdateAddress(addressModel: AddressUIState?) {
        if (addressModel != null) {
            updateState {
                copy(
                    currentLocation = Coordinates(
                        latitude = addressModel.coordinates.latitude,
                        longitude = addressModel.coordinates.longitude
                    ).toUiState(),
                    address = addressModel.addressDetails,
                    isMapLocked = true,
                    animateToCurrentLocation = true,
                    isMainAddress = addressModel.isMainAddress
                )
            }
        }
    }

    override fun onClickMap(
        coordinates: PickLocationScreenUIState.CoordinatesUiState,
        pointerLocation: DpOffset
    ) {
        updateState {
            copy(
                isMapLocked = true,
                currentLocation = coordinates,
                pointerLocation = pointerLocation
            )
        }
        getLocationName()
    }

    private fun getLocationName() {
        tryToExecute(
            function = ::onGetLocationName,
            onSuccess = ::onGetLocationNameSuccess,
            onError = ::onError,
            dispatcher = Dispatchers.Main
        )
    }

    private suspend fun onGetLocationName(): String {
        return mobileLocationRepository.getLocationName(state.value.currentLocation.toEntity())
    }

    private fun onGetLocationNameSuccess(address: String) {
        updateState { copy(address = address) }
        changeIsConfirmEnabled()
    }

    override fun onMoveCamera(
        cameraPosition: CameraPosition
    ) {
        updateState { copy(cameraPosition = cameraPosition, animateToCurrentLocation = false) }
    }

    override fun onClickEdit() {
        updateState {
            copy(
                address = "",
                currentLocation = PickLocationScreenUIState.CoordinatesUiState(),
                pointerLocation = null,
                isMapLocked = false,
                animateToCurrentLocation = false
            )
        }
        changeIsConfirmEnabled()
    }

    override fun onClickGps() {
        tryToExecute(
            function = ::onGpsFetch,
            onSuccess = ::onClickGpsSuccess,
            onError = ::onClickGpsError,
            dispatcher = Dispatchers.Main
        )
    }

    private suspend fun onGpsFetch(): Coordinates? {
        updateState { copy(isGpsButtonLoading = true) }
        return mobileLocationRepository.getCurrentLocation()
    }

    private fun onClickGpsSuccess(
        coordinates: Coordinates?
    ) {
        if (coordinates != null) {
            updateState {
                copy(
                    currentLocation = coordinates.toUiState(),
                    isMapLocked = true,
                    animateToCurrentLocation = true,
                    isGpsButtonLoading = false
                )
            }
            getLocationName()
        }
    }

    private fun onClickGpsError(errorState: ErrorState) {
        checkLocationEnable()
    }

    private fun checkLocationEnable() {
        tryToExecute(
            function = { locationForegroundHandler.checkPermission() },
            onSuccess = ::checkLocationEnableSuccess,
            ::onError,
            dispatcher = dispatcher
        )
    }

    private fun checkLocationEnableSuccess(permissionState: PermissionState) {
        when (permissionState) {
            PermissionState.GRANTED -> {
                updateState {
                    copy(
                        errorMessage = "Location is turned off",
                        isGpsButtonLoading = false
                    )
                }
            }

            PermissionState.DENIED -> {
                navigateToEnableLocation()
            }

            PermissionState.NOT_DETERMINED -> {
                navigateToEnableLocation()
            }
        }
    }

    private fun onError(errorState: ErrorState) {
        updateState {
            copy(
                errorMessage = mapErrorToMessage(errorState),
                isGpsButtonLoading = false,
                address = ""
            )
        }
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
                    coordinates = CoordinatesUiState(state.value.currentLocation.latitude ,
                        state.value.currentLocation.longitude) ,
                    addressDetails = state.value.address,
                    isMainAddress = state.value.isMainAddress
                )
            )
        )
    }

    override fun onClearErrorMessage() {
        updateState { copy(errorMessage = null) }
    }

    override fun onClickBack() {
        sendNewEffect(PickLocationScreenUIEffect.NavigateBack)
    }

    override fun onSetAnchorLocation(pointerLocation: DpOffset) {
        updateState { copy(pointerLocation = pointerLocation, animateToCurrentLocation = false) }
    }

    private fun changeIsConfirmEnabled() {
        if (state.value.isMapLocked && state.value.address.isNotBlank()) {
            updateState { copy(isConfirmEnabled = true) }
        } else {
            updateState { copy(isConfirmEnabled = false) }
        }
    }
}