package net.thechance.mena.identity.presentation.screen.register.otp

sealed interface RegisterOtpUIEffect {
    data object NavigateToCreatePassword : RegisterOtpUIEffect
}