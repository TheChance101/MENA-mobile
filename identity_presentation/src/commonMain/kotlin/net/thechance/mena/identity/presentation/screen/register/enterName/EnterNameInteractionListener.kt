package net.thechance.mena.identity.presentation.screen.register.enterName

import net.thechance.mena.identity.presentation.base.BaseInteractionListener

interface EnterNameInteractionListener : BaseInteractionListener {
    fun onFirstNameChange(name: String)
    fun onLastNameChange(name: String)
    fun onUsernameChange(username: String)
    fun onClickNext()
    fun onClearErrorMessage()
}