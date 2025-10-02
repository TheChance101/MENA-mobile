package net.thechance.mena.identity.presentation.screen.forgetPasswordOtp

import net.thechance.mena.identity.presentation.base.BaseInteractionListener

interface OtpScreenInteractionListener: BaseInteractionListener {
    fun onClickBack()
    fun onClickVerify()
    fun onClickResend()
    fun onChangeOtp(otp: String)
    fun onClearErrorMessage()
}