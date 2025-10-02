package net.thechance.mena.identity.presentation.screen.resetPassword

data class ResetPasswordScreenUIState(
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isNewPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isResetEnabled: Boolean = false,
    val errorMessage: String? = null,
)