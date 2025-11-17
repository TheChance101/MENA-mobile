package net.thechance.mena.identity.presentation.feature.authenticationFlow.register.enterName

import net.thechance.mena.identity.presentation.core.base.BaseInteractionListener

interface EnterNameInteractionListener : BaseInteractionListener {
    fun onChangeFirstName(name: String)
    fun onLastNameChange(name: String)
    fun onUsernameChange(username: String)
    fun onClickNext()
    fun onClearErrorMessage()
}