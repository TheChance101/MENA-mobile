package net.thechance.mena.admin_panel.presentation.screen.login

import net.thechance.mena.admin_panel.presentation.base.ErrorState

data class LoginScreenState(
    val errorState: ErrorState? = null,
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoginBtnLoading: Boolean = false,
){
    val isLoginBtnEnabled : Boolean
        get() = password.length > 8 && username.length > 8
}