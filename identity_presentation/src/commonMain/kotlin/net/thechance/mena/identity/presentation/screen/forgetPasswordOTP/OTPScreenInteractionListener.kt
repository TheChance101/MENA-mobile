package net.thechance.mena.identity.presentation.screen.forgetPasswordOTP

import net.thechance.mena.identity.presentation.base.BaseInteractionListener

interface OTPScreenInteractionListener: BaseInteractionListener {
    fun onBackClicked()
    fun onVerifyClicked()
    fun onResendClicked()
    fun onOtpChanged(otp: String)
    fun clearErrorMessage()
}