package net.thechance.mena.identity.presentation.feature.authenticationFlow.register.createPassword

import net.thechance.mena.identity.presentation.core.base.BaseInteractionListener

interface CreatePasswordInteractionListener : BaseInteractionListener {
    fun onChangeNewPassword(password: String)
    fun onChangeConfirmPassword(password: String)
    fun onToggleNewPasswordVisibility()
    fun onToggleConfirmPasswordVisibility()
    fun onClickCreatePassword()
    fun onClearErrorMessage()
}