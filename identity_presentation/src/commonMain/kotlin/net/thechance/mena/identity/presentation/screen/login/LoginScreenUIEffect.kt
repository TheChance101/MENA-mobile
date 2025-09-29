package net.thechance.mena.identity.presentation.screen.login

sealed class LoginScreenUIEffect {
    data object NavigateToRegister : LoginScreenUIEffect()
    data object NavigateToForgotPassword : LoginScreenUIEffect()
    data object NavigateToHome : LoginScreenUIEffect()



}