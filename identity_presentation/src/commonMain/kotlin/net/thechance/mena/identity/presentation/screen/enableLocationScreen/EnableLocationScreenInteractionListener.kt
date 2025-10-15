package net.thechance.mena.identity.presentation.screen.enableLocationScreen

import net.thechance.mena.identity.presentation.base.BaseInteractionListener

interface EnableLocationScreenInteractionListener : BaseInteractionListener {
    fun onClickBack()
    fun onClickEnablePermission()
    fun onClearErrorMessage()
}