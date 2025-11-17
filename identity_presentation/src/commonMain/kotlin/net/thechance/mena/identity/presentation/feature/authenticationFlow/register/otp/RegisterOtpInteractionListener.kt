package net.thechance.mena.identity.presentation.feature.authenticationFlow.register.otp

import net.thechance.mena.identity.presentation.core.base.BaseInteractionListener

interface RegisterOtpInteractionListener : BaseInteractionListener {
    fun onClickVerify()
    fun onClickResend()
    fun onChangeOtp(otp: String)
    fun onClearErrorMessage()
}