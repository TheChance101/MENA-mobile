package net.thechance.mena.identity.presentation.screen.forget_password

import net.thechance.mena.identity.presentation.base.BaseScreenModel

class ForgetPasswordScreenModel :
    BaseScreenModel<ForgetPasswordScreenUIState, ForgetPasswordScreenUIEffect>(ForgetPasswordScreenUIState()),
    ForgetPasswordScreenInteractionListener {

}

