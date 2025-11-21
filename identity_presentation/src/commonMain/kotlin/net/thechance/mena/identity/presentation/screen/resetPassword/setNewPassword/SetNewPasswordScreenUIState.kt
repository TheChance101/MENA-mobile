package net.thechance.mena.identity.presentation.screen.resetPassword.setNewPassword

import org.jetbrains.compose.resources.StringResource

data class SetNewPasswordScreenUIState(
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isNewPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isResetEnabled: Boolean = false,
    val newPasswordErrorMessage: StringResource? = null,
    val confirmPasswordErrorMessage: StringResource? = null,
    val isDialogVisible: Boolean = false
)