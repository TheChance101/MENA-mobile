package net.thechance.mena.admin_panel.presentation.screen.login

import net.thechance.mena.admin_panel.presentation.model.SnackBarState

data class LoginScreenState(
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoginBtnLoading: Boolean = false,
    val snackBar: SnackBarState = SnackBarState()
){
    val isLoginBtnEnabled : Boolean
        get() = password.isNotEmpty() && username.isNotEmpty()
}