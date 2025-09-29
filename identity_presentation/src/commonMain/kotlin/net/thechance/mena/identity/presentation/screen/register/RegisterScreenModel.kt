package net.thechance.mena.identity.presentation.screen.register

import kotlinx.coroutines.CoroutineScope
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import cafe.adriel.voyager.core.model.screenModelScope


class RegisterScreenModel :
    BaseScreenModel<RegisterScreenUIState, RegisterScreenUIEffect>(RegisterScreenUIState()),
    RegisterScreenInteractionListener {
    override val viewModelScope: CoroutineScope
        get() = screenModelScope
}

