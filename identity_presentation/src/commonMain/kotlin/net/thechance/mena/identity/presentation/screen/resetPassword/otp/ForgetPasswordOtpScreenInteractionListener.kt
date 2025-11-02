package net.thechance.mena.identity.presentation.screen.resetPassword.otp

import net.thechance.mena.identity.presentation.base.BaseInteractionListener

interface ForgetPasswordOtpScreenInteractionListener: BaseInteractionListener {
    fun onClickBack()
    fun onClickVerify()
    fun onClickResend()
    fun onChangeOtp(otp: String)
    fun onClearErrorMessage()
}