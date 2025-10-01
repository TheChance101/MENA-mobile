package net.thechance.mena.identity.presentation.screen.register

import net.thechance.mena.identity.presentation.base.BaseScreenModel


class RegisterScreenModel :
    BaseScreenModel<RegisterScreenUIState, RegisterScreenUIEffect>(RegisterScreenUIState()),
    RegisterScreenInteractionListener {

}

