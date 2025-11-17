package net.thechance.mena.identity.presentation.feature.locationFlow.enableLocationScreen

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.domain.exception.LocationException
import net.thechance.mena.identity.presentation.core.base.BaseScreenModel
import net.thechance.mena.identity.presentation.core.base.errorState.ErrorState
import net.thechance.mena.identity.presentation.core.mapper.mapErrorToMessage
import net.thechance.mena.identity.presentation.core.mapper.mapLocationErrorToMessage
import net.thechance.mena.identity.presentation.core.util.permissionHandler.PermissionHandler
import net.thechance.mena.identity.presentation.core.util.permissionHandler.PermissionState
import net.thechance.mena.identity.presentation.feature.locationFlow.shared.handleLocationException
import org.jetbrains.compose.resources.StringResource

class EnableLocationScreenViewModel(
    private val locationForegroundHandler: PermissionHandler,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    BaseScreenModel<EnableLocationScreenUIState, EnableLocationScreenUIEffect>(
        EnableLocationScreenUIState()
    ),
    EnableLocationScreenInteractionListener {
    override fun onClickBack() {
        sendNewEffect(EnableLocationScreenUIEffect.NavigateBack)
    }

    override fun onClickEnablePermission() {
        locationForegroundHandler.openSettingPage()
        checkIfEnabledPermission()
    }

    override fun onClearErrorMessage() {
        updateState { copy(errorMessage = null) }
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
        updateState { copy(errorMessage = mapErrorMessage(throwable)) }
    }

    private fun mapErrorMessage(throwable: Throwable): StringResource{
        return when (throwable) {
            is LocationException -> mapLocationErrorToMessage(handleLocationException(throwable))
            else -> mapErrorToMessage(ErrorState.GenericError(throwable))
        }
    }
}