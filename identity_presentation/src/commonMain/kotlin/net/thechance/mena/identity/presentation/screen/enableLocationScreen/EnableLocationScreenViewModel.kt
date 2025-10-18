package net.thechance.mena.identity.presentation.screen.enableLocationScreen

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.ErrorState
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionHandler
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState

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
            onNewValue = { checkIfEnabledPermissionSuccess(it) },
            onError = ::onError,
            dispatcher = dispatcher
        )
    }

    private fun checkIfEnabledPermissionSuccess(permissionState: PermissionState) {
        if (permissionState == PermissionState.GRANTED) {
            sendNewEffect(EnableLocationScreenUIEffect.NavigateBack)
        }
    }

    private fun onError(errorState: ErrorState) {
        updateState { copy(errorMessage = mapErrorToMessage(errorState)) }
    }
}