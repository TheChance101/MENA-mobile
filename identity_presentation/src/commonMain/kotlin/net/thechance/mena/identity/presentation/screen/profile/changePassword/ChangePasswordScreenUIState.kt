package net.thechance.mena.identity.presentation.screen.profile.changePassword

import org.jetbrains.compose.resources.StringResource

data class ChangePasswordScreenUIState(
    val currentPasswordUIState: CurrentPasswordContentUIState = CurrentPasswordContentUIState(),
    val newPasswordUIState: NewPasswordContentUIState = NewPasswordContentUIState(),
    val currentPage: PasswordPage = PasswordPage.CURRENT_PASSWORD,
    val isLoading: Boolean = false,
){
    data class CurrentPasswordContentUIState(
        val currentPassword: String = "",
        val isCurrentPasswordVisible: Boolean = false,
        val isContinueEnabled: Boolean = false,
        val currentPasswordErrorMessage: StringResource? = null,
    )

    data class NewPasswordContentUIState(
        val newPassword: String = "",
        val confirmPassword: String = "",
        val isNewPasswordVisible: Boolean = false,
        val isConfirmPasswordVisible: Boolean = false,
        val isSaveEnabled: Boolean = false,
        val newPasswordErrorMessage: StringResource? = null,
        val confirmPasswordErrorMessage: StringResource? = null,
    )

}

enum class PasswordPage(val index:Int) {
    CURRENT_PASSWORD(0),
    NEW_PASSWORD(1)
}


