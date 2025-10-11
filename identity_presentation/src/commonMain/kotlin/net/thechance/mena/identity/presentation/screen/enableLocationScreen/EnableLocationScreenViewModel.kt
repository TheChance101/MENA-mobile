package net.thechance.mena.identity.presentation.screen.enableLocationScreen

import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.components.util.settingsOpener.SettingsOpener

class EnableLocationScreenViewModel(
    private val settingsOpener: SettingsOpener
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