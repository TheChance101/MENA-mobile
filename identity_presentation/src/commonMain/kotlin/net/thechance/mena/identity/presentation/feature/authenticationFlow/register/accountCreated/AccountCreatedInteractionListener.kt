package net.thechance.mena.identity.presentation.feature.authenticationFlow.register.accountCreated

import net.thechance.mena.identity.presentation.core.base.BaseInteractionListener

interface AccountCreatedInteractionListener : BaseInteractionListener {
    fun onClickGoToHome()
}