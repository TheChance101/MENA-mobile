package net.thechance.mena.identity.presentation.screen.resetPassword.setNewPassword

import net.thechance.mena.identity.presentation.base.BaseInteractionListener

interface SetNewPasswordScreenInteractionListener : BaseInteractionListener {
    fun onChangeNewPassword(password: String)
    fun onChangeConfirmPassword(password: String)
    fun onToggleNewPasswordVisibility()
    fun onToggleConfirmPasswordVisibility()
    fun onClickResetPassword()
    fun onClickBack()
    fun onClickOk()
}