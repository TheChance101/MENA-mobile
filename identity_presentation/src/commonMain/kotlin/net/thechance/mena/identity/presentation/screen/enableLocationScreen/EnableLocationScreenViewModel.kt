package net.thechance.mena.identity.presentation.screen.enableLocationScreen

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionHandler

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
    }
}