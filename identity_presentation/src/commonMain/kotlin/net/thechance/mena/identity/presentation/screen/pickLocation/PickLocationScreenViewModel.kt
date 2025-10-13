package net.thechance.mena.identity.presentation.screen.pickLocation

import androidx.compose.ui.unit.DpOffset
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.domain.entity.Coordinates
import net.thechance.mena.identity.domain.repository.LocationRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.ErrorState
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionHandler
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState
import org.maplibre.compose.camera.CameraPosition

class PickLocationScreenViewModel(
    private val locationRepository: LocationRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val locationForegroundHandler: PermissionHandler,
    private val locationServiceHandler: PermissionHandler
) : BaseScreenModel<PickLocationScreenUIState, PickLocationScreenUIEffect>(PickLocationScreenUIState()),
    PickLocationScreenInteractionListener {
    override fun onClickMap(
        coordinates: PickLocationScreenUIState.CoordinatesUiState,
        pointerLocation: DpOffset
    ) {
        tryToExecute(
            function = { onClickMapBlock(coordinates, pointerLocation) },
            onSuccess = ::onMapClickedSuccess ,
            onError = ::onError,
            dispatcher = dispatcher
        )
    }

    private suspend fun onClickMapBlock(
        coordinates: PickLocationScreenUIState.CoordinatesUiState,
        pointerLocation: DpOffset
    ): String {
        updateState {
            copy(
                currentLocation = coordinates,
                pointerLocation = pointerLocation,
                isMapLocked = true
            )
        }
        return locationRepository.getLocationName(coordinates.toEntity())
    }

    private fun onMapClickedSuccess(address: String) {
        updateState { copy(address = address) }
        changeIsConfirmEnabled()
    }

    override fun onCameraMoved(
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
        checkLocationPermission()
    }

    fun checkLocationPermission() {
        tryToExecute(
            function = { locationForegroundHandler.checkPermission() },
            onSuccess = ::onCheckForegroundPermissionsSuccess,
            onError = {}
        )
    }
    private fun onCheckForegroundPermissionsSuccess(permissionState: PermissionState) {
        when (permissionState) {
            PermissionState.NOT_DETERMINED -> {

            }
            PermissionState.GRANTED -> {
                checkLocationServicePermission()
            }

            PermissionState.DENIED -> {
                navigateToEnableLocation()
            }
        }
    }

    private fun checkLocationServicePermission() {
        tryToExecute(
            function = { locationServiceHandler.checkPermission() },
            onSuccess = ::onCheckPermissionsSuccess ,
            onError = {}
        )
    }

    private fun onCheckPermissionsSuccess(permissionState: PermissionState) {
        when (permissionState) {
            PermissionState.NOT_DETERMINED -> {
            }
            PermissionState.GRANTED -> {
                tryToExecute(
                    function = ::onGpsFetch,
                    onSuccess = ::onGpsClickSuccess,
                    onError = ::onError,
                    dispatcher = dispatcher
                )
            }
            PermissionState.DENIED -> {
                updateState { copy(errorMessage = "GPS is off") }
            }
        }
    }

    private suspend fun onGpsFetch(): Coordinates? {
        updateState { copy(isGpsButtonLoading = true) }
        return locationRepository.getCurrentLocation()
    }

    private fun onError(errorState: ErrorState) {
        when (errorState) {
            is ErrorState.NoLocationPermission -> navigateToEnableLocation()
            else -> updateState {
                copy(
                    errorMessage = mapErrorToMessage(errorState),
                    isGpsButtonLoading = false
                )
            }
        }
    }

    private fun navigateToEnableLocation(){
        sendNewEffect(PickLocationScreenUIEffect.NavigateToEnableLocation)
        updateState { copy(isGpsButtonLoading = false) }
    }

    private fun onGpsClickSuccess(
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
        }
    }

    override fun onClickConfirm() {
        sendNewEffect(
            PickLocationScreenUIEffect.NavigateToAddLocation(
                latitude = state.value.currentLocation.latitude,
                longitude = state.value.currentLocation.longitude,
                address = state.value.address
            )
        )
    }

    override fun onClearErrorMessage() {
        updateState { copy(errorMessage = null) }
    }

    override fun onClickBack() {
        sendNewEffect(PickLocationScreenUIEffect.NavigateBack)
    }

    private fun changeIsConfirmEnabled() {
        if (state.value.isMapLocked && state.value.address.isNotBlank()) {
            updateState { copy(isConfirmEnabled = true) }
        } else {
            updateState { copy(isConfirmEnabled = false) }
        }
    }
}