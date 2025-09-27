package net.thechance.mena.identity.presentation.screen.forget_password_otp

import net.thechance.mena.identity.presentation.base.BaseInteractionListener

interface OTPScreenInteractionListener: BaseInteractionListener {
    fun onBackClicked()
    fun onVerifyClicked()
    fun onResendClicked()
    fun onOtpChanged(otp: String)
    fun clearErrorMessage()
}