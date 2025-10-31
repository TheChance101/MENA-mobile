package net.thechance.mena.identity.presentation.screen.notImplemented

import net.thechance.mena.identity.presentation.base.BaseScreenModel

class NotImplementedScreenViewModel :
    BaseScreenModel<NotImplementedScreenUIState, NotImplementedScreenUIEffect>(
        NotImplementedScreenUIState()
    ), NotImplementedScreenInteractionListener {

    override fun onBackButtonClicked() {
        sendNewEffect(NotImplementedScreenUIEffect.NavigateBack)
    }
}