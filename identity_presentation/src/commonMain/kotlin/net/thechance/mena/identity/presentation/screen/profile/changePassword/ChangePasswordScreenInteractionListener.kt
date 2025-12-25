package net.thechance.mena.identity.presentation.screen.profile.changePassword

import net.thechance.mena.identity.presentation.base.BaseInteractionListener

interface ChangePasswordScreenInteractionListener : BaseInteractionListener{

    fun onClickBack()
    fun onClickContinue()
    fun onClickSave()
    fun onChangeCurrentPassword(newValue:String)
    fun onChangeNewPassword(newValue:String)
    fun onChangeConfirmPassword(newValue:String)
    fun onToggleCurrentPasswordVisibility()
    fun onToggleNewPasswordVisibility()
    fun onToggleConfirmPasswordVisibility()
}