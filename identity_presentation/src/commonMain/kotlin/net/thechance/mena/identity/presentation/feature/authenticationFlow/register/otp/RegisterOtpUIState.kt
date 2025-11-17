package net.thechance.mena.identity.presentation.feature.authenticationFlow.register.otp

import org.jetbrains.compose.resources.StringResource

data class RegisterOtpUIState(
    val otpValue: String = "",
    val isVerifyEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val isResendEnabled: Boolean = true,
    val timer: String = "",
    val errorMessage: StringResource? = null
)