package net.thechance.mena.identity.presentation.feature.authenticationFlow.resetPassword.setNewPassword

import net.thechance.mena.identity.presentation.core.base.BaseInteractionListener

interface SetNewPasswordScreenInteractionListener : BaseInteractionListener {
    fun onChangeNewPassword(password: String)
    fun onChangeConfirmPassword(password: String)
    fun onToggleNewPasswordVisibility()
    fun onToggleConfirmPasswordVisibility()
    fun onClickResetPassword()
    fun onClickBack()
    fun onClearErrorMessage()
    fun onClickOk()
}