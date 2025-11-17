package net.thechance.mena.identity.presentation.feature.authenticationFlow.resetPassword.otp

import net.thechance.mena.identity.presentation.core.base.BaseInteractionListener

interface ResetPasswordOtpScreenInteractionListener: BaseInteractionListener {
    fun onClickBack()
    fun onClickVerify()
    fun onClickResend()
    fun onChangeOtp(otp: String)
    fun onClearErrorMessage()
}