package net.thechance.mena.identity.presentation.screen.accountCreated

import net.thechance.mena.identity.presentation.base.BaseInteractionListener

interface AccountCreatedInteractionListener : BaseInteractionListener {
    fun onClickGoToHome()
    fun clearErrorMessage()
}