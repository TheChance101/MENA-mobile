package net.thechance.mena.admin_panel.presentation.screen.login

interface LoginInteractionListener {
    fun onUsernameChanged(username: String)
    fun onPasswordChanged(password: String)
    fun onPasswordVisibilityToggled()
    fun onLoginButtonClicked()
}