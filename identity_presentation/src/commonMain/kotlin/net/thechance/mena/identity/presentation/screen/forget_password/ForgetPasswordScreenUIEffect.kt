package net.thechance.mena.identity.presentation.screen.forget_password

sealed class ForgetPasswordScreenUIEffect {
    data object NavigateToOTP : ForgetPasswordScreenUIEffect()
    data object NavigateBack : ForgetPasswordScreenUIEffect()
}