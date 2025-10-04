package net.thechance.mena.identity.presentation.screen.resetPassword

import net.thechance.mena.identity.presentation.base.BaseInteractionListener

interface ResetPasswordScreenInteractionListener : BaseInteractionListener {
    fun onChangeNewPassword(password: String)
    fun onChangeConfirmPassword(password: String)
    fun onToggleNewPasswordVisibility()
    fun onToggleConfirmPasswordVisibility()
    fun onClickResetPassword()
    fun onClickBack()
    fun onClearErrorMessage()
    fun onClickOk()
}