package net.thechance.mena.identity.presentation.screen.resetPassword.otp

import org.jetbrains.compose.resources.StringResource

sealed interface ResetPasswordOtpScreenUIEffect {
    data object NavigateToResetPassword : ResetPasswordOtpScreenUIEffect
    data object NavigateBack : ResetPasswordOtpScreenUIEffect
    data class ShowSnackBarError(val errorStringResource: StringResource) :
        ResetPasswordOtpScreenUIEffect
}