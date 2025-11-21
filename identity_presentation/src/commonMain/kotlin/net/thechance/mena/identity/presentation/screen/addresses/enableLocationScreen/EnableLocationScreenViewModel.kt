package net.thechance.mena.identity.presentation.screen.addresses.enableLocationScreen

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.domain.exception.LocationException
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.errorState.ErrorState
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import net.thechance.mena.identity.presentation.mapper.mapLocationErrorToMessage
import net.thechance.mena.identity.presentation.screen.addresses.shared.handleLocationException
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionHandler
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState
import org.jetbrains.compose.resources.StringResource

class EnableLocationScreenViewModel(
    private val locationForegroundHandler: PermissionHandler,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    BaseScreenModel<EnableLocationScreenUIState, EnableLocationScreenUIEffect>(
        EnableLocationScreenUIState
    ),
    EnableLocationScreenInteractionListener {
    override fun onClickBack() {
        sendNewEffect(EnableLocationScreenUIEffect.NavigateBack)
    }

    override fun onClickEnablePermission() {
        locationForegroundHandler.openSettingPage()
        checkIfEnabledPermission()
    }

    private fun checkIfEnabledPermission() {
        tryToCollect(
            function = { locationForegroundHandler.checkPermissionFlow() },
            onNewValue = ::checkIfEnabledPermissionSuccess,
            onError = ::onPermissionError,
            dispatcher = dispatcher
        )
    }

    private fun checkIfEnabledPermissionSuccess(permissionState: PermissionState) {
        if (permissionState == PermissionState.GRANTED) {
            sendNewEffect(EnableLocationScreenUIEffect.NavigateBack)
        }
    }

    private fun onPermissionError(throwable: Throwable) {
        sendNewEffect(
            EnableLocationScreenUIEffect.ShowSnackBarError(
                errorStringResource = mapErrorMessage(throwable)
            )
        )
    }

    private fun mapErrorMessage(throwable: Throwable): StringResource {
        return when (throwable) {
            is LocationException -> mapLocationErrorToMessage(handleLocationException(throwable))
            else -> mapErrorToMessage(ErrorState.GenericError(throwable))
        }
    }
}