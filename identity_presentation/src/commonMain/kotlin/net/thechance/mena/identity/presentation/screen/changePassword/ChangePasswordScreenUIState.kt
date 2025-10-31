package net.thechance.mena.identity.presentation.screen.changePassword

import org.jetbrains.compose.resources.StringResource

data class ChangePasswordScreenUIState(
    val currentPasswordUIState: CurrentPasswordContentUIState,
    val newPasswordUIState: NewPasswordContentUIState,
    val errorMessage: StringResource? = null,
    val isLoading: Boolean = false,
)

data class CurrentPasswordContentUIState(
    val currentPassword: String = "",
    val isCurrentPasswordVisible: Boolean = false,
    val isContinueEnabled: Boolean = false,
    val currentPasswordErrorMessage: String? = null,
)

data class NewPasswordContentUIState(
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isNewPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isSaveEnabled: Boolean = false,
    val newPasswordErrorMessage: String? = null,
    val confirmPasswordErrorMessage: String? = null,
)