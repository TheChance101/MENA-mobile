package net.thechance.mena.identity.presentation.screen.forgetPasswordOtp

import net.thechance.mena.identity.presentation.base.BaseInteractionListener

interface OtpScreenInteractionListener: BaseInteractionListener {
    fun onBackClicked()
    fun onVerifyClicked()
    fun onResendClicked()
    fun onOtpChanged(otp: String)
    fun clearErrorMessage()
}