package net.thechance.mena.identity.presentation.screen.register.createPassword

import org.jetbrains.compose.resources.StringResource

data class CreatePasswordUIState(
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isNewPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isCreateEnabled: Boolean = false,
    val newPasswordErrorMessage: StringResource? = null,
    val confirmPasswordErrorMessage: StringResource? = null
)