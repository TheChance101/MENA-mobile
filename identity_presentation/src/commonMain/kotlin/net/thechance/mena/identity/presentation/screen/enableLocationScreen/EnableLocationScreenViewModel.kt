package net.thechance.mena.identity.presentation.screen.enableLocationScreen

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.util.settingsOpener.SettingsOpener

class EnableLocationScreenViewModel(
    private val settingsOpener: SettingsOpener,
    val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    BaseScreenModel<EnableLocationScreenUIState, EnableLocationScreenUIEffect>(
        EnableLocationScreenUIState()
    ),
    EnableLocationScreenInteractionListener {
    override fun onClickBack() {
        sendNewEffect(EnableLocationScreenUIEffect.NavigateBack)
    }

    override fun onClickEnablePermission() {
        settingsOpener.openSettings()
    }
}