package net.thechance.mena.identity.presentation.screen.forget_password

import kotlinx.coroutines.CoroutineScope
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import cafe.adriel.voyager.core.model.screenModelScope

class ForgetPasswordScreenModel :
    BaseScreenModel<ForgetPasswordScreenUIState, ForgetPasswordScreenUIEffect>(ForgetPasswordScreenUIState()),
    ForgetPasswordScreenInteractionListener {
    override val viewModelScope: CoroutineScope
        get() = screenModelScope
}

