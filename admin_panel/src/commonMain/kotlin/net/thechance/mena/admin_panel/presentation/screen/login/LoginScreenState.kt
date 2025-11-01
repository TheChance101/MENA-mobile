package net.thechance.mena.admin_panel.presentation.screen.login

import net.thechance.mena.admin_panel.presentation.base.ErrorState
import net.thechance.mena.admin_panel.presentation.model.SnackBarState

data class LoginScreenState(
    val errorState: ErrorState? = null,
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoginBtnLoading: Boolean = false,
    val usernameErrorMsg: String? = null,
    val passwordErrorMsg: String? = null,
    val snackBar: SnackBarState = SnackBarState()
){
    val isLoginBtnEnabled : Boolean
        get() = password.isNotEmpty() && username.isNotEmpty()
}