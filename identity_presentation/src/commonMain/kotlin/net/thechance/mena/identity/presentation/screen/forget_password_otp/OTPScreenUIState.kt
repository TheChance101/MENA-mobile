package net.thechance.mena.identity.presentation.screen.forget_password_otp

data class OTPScreenUIState (
    val otpValue: String = "",
    val isOtpValid: Boolean = false,
    val isVerifyEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val isResendEnabled: Boolean = true,
    val timer: String = "",
    val errorMessage: String? = null
)