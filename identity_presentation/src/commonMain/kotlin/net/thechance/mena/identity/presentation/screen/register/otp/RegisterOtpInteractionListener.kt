package net.thechance.mena.identity.presentation.screen.register.otp

import net.thechance.mena.identity.presentation.base.BaseInteractionListener

interface RegisterOtpInteractionListener : BaseInteractionListener {
    fun onClickVerify()
    fun onClickResend()
    fun onChangeOtp(otp: String)
}