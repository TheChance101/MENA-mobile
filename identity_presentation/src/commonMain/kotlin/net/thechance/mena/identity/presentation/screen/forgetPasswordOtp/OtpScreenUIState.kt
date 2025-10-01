package net.thechance.mena.identity.presentation.screen.forgetPasswordOtp

data class OtpScreenUIState (
    val otpValue: String = "",
    val isVerifyEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val isResendEnabled: Boolean = true,
    val timer: String = "",
    val errorMessage: String? = null
)