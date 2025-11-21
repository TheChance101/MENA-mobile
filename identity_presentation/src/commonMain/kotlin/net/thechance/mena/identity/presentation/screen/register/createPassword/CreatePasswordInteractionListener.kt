package net.thechance.mena.identity.presentation.screen.register.createPassword

import net.thechance.mena.identity.presentation.base.BaseInteractionListener

interface CreatePasswordInteractionListener : BaseInteractionListener {
    fun onChangeNewPassword(password: String)
    fun onChangeConfirmPassword(password: String)
    fun onToggleNewPasswordVisibility()
    fun onToggleConfirmPasswordVisibility()
    fun onClickCreatePassword()
}