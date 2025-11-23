package net.thechance.mena.identity.presentation.screen.register.enterName

import net.thechance.mena.identity.presentation.base.BaseInteractionListener

interface EnterNameInteractionListener : BaseInteractionListener {
    fun onChangeFirstName(name: String)
    fun onLastNameChange(name: String)
    fun onUsernameChange(username: String)
    fun onClickNext()
}