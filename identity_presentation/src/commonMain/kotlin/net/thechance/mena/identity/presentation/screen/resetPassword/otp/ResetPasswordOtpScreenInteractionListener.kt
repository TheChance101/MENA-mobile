package net.thechance.mena.identity.presentation.screen.resetPassword.otp

import net.thechance.mena.identity.presentation.base.BaseInteractionListener

interface ResetPasswordOtpScreenInteractionListener: BaseInteractionListener {
    fun onClickBack()
    fun onClickVerify()
    fun onClickResend()
    fun onChangeOtp(otp: String)
}