package net.thechance.mena.identity.presentation.screen.resetPassword

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error_password_mismatch
import net.thechance.mena.identity.domain.repository.ResetPasswordRepository
import net.thechance.mena.identity.domain.useCase.validation.mobileNumber.PasswordValidator
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.ErrorState
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage

class ResetPasswordScreenViewModel(
    private val passwordValidator: PasswordValidator,
    private val resetPasswordRepository: ResetPasswordRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseScreenModel<ResetPasswordScreenUIState, ResetPasswordScreenUIEffect>(
    ResetPasswordScreenUIState()
), ResetPasswordScreenInteractionListener {

    override fun onChangeNewPassword(password: String) {
        updateState { copy(newPassword = password) }
        checkResetButtonEnabled()
    }

    override fun onChangeConfirmPassword(password: String) {
        updateState {
            copy(
                confirmPassword = password,
                confirmPasswordErrorMessage = if (password != newPassword)
                    "Confirm password doesn't match the new password"
                else null,
            )
        }
        checkResetButtonEnabled()
    }

    override fun onToggleNewPasswordVisibility() {
        updateState { copy(isNewPasswordVisible = !isNewPasswordVisible) }
    }

    override fun onToggleConfirmPasswordVisibility() {
        updateState { copy(isConfirmPasswordVisible = !isConfirmPasswordVisible) }
    }

    override fun onClickBack() {
        sendNewEffect(ResetPasswordScreenUIEffect.NavigateBackToLogin)
    }

    override fun onClearErrorMessage() {
        updateState { copy(errorMessage = null) }
    }

    override fun onClickOk() {
        sendNewEffect(ResetPasswordScreenUIEffect.NavigateBackToLogin)
        updateState { copy(isDialogVisible = false) }
    }

    override fun onClickResetPassword() {
        if (state.value.newPassword != state.value.confirmPassword) {
            updateState { copy(errorMessage = Res.string.error_password_mismatch) }
            return
        }

        updateState { copy(isLoading = true, errorMessage = null) }

        tryToExecute(
            function = ::onResetPassword,
            onSuccess = ::onResetPasswordSuccess,
            onError = ::onErrorAccrue,
            dispatcher = dispatcher
        )
    }

    private suspend fun onResetPassword() {
        resetPasswordRepository.resetPassword(
            state.value.confirmPassword,
            state.value.newPassword,
        )
    }

    private fun onResetPasswordSuccess() {
        updateState { copy(isLoading = false, isDialogVisible = true) }
    }

    private fun onErrorAccrue(errorState: ErrorState) {
        updateState {
            copy(
                isLoading = false,
                errorMessage = mapErrorToMessage(errorState)
            )
        }
    }

    private fun checkResetButtonEnabled() {
        updateState {
            val isPasswordsMatch = newPassword.isNotBlank() && newPassword == confirmPassword
            val isPasswordSecure = passwordValidator.isValid(newPassword)

            copy(
                newPasswordErrorMessage = if (!isPasswordSecure)
                    "Password must be at least 8 characters long and contain at least one uppercase letter and one digit."
                else null,
                isResetEnabled = isPasswordsMatch && isPasswordSecure
            )
        }
    }

}