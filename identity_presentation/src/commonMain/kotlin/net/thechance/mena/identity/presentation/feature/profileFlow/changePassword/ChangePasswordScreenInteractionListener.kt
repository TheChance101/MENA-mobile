package net.thechance.mena.identity.presentation.feature.profileFlow.changePassword

import net.thechance.mena.identity.presentation.core.base.BaseInteractionListener

interface ChangePasswordScreenInteractionListener : BaseInteractionListener {
    fun onClickBack()
    fun onClickContinue()
    fun onClickSave()
    fun onChangeCurrentPassword(newValue:String)
    fun onChangeNewPassword(newValue:String)
    fun onChangeConfirmPassword(newValue:String)
    fun onToggleCurrentPasswordVisibility()
    fun onToggleNewPasswordVisibility()
    fun onToggleConfirmPasswordVisibility()
    fun onClearErrorMessage()
}