package net.thechance.mena.admin_panel.presentation.screen.login

import net.thechance.mena.admin_panel.presentation.base.ErrorState
import net.thechance.mena.admin_panel.presentation.model.SnackBarState

data class LoginScreenState(
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoginButtonLoading: Boolean = false,
    val snackBar: SnackBarState = SnackBarState()
){
    val isLoginButtonEnabled : Boolean
        get() = password.length >= 8 && username.isNotEmpty()
}
interface LoginErrorState : ErrorState {
    data object InvalidCredentials : ErrorState
}