package net.thechance.mena.identity.presentation.screen.resetPassword.setNewPassword

import org.jetbrains.compose.resources.StringResource

data class SetNewPasswordScreenUIState(
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isNewPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isResetEnabled: Boolean = false,
    val errorMessage: StringResource? = null,
    val newPasswordErrorMessage: String? = null,
    val confirmPasswordErrorMessage: String? = null,
    val isDialogVisible: Boolean = false
)