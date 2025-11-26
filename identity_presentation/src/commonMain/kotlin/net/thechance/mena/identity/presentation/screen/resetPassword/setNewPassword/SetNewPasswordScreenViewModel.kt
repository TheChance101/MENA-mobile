package net.thechance.mena.identity.presentation.screen.resetPassword.setNewPassword

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error_confirm_password_not_match
import mena.identity_presentation.generated.resources.error_password_mismatch
import mena.identity_presentation.generated.resources.error_password_validation
import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.repository.ResetPasswordRepository
import net.thechance.mena.identity.domain.useCase.validation.mobileNumber.PasswordValidator
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.errorState.ErrorState
import net.thechance.mena.identity.presentation.mapper.mapAuthenticationErrorToMessage
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import org.jetbrains.compose.resources.StringResource

class SetNewPasswordScreenViewModel(
    private val passwordValidator: PasswordValidator,
    private val resetPasswordRepository: ResetPasswordRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseScreenModel<SetNewPasswordScreenUIState, SetNewPasswordScreenUIEffect>(
    SetNewPasswordScreenUIState()
), SetNewPasswordScreenInteractionListener {

    override fun onChangeNewPassword(password: String) {
        updateState { copy(newPassword = password) }
        checkResetButtonEnabled()
    }

    override fun onChangeConfirmPassword(password: String) {
        updateState {
            copy(
                confirmPassword = password,
                confirmPasswordErrorMessage =
                    if (!passwordValidator.isPasswordMatch(newPassword, password))
                        Res.string.error_confirm_password_not_match
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
        sendNewEffect(SetNewPasswordScreenUIEffect.NavigateBackToLogin)
    }

    override fun onClickOk() {
        sendNewEffect(SetNewPasswordScreenUIEffect.NavigateBackToLogin)
        updateState { copy(isDialogVisible = false) }
    }

    override fun onClickResetPassword() {
        if (state.value.newPassword != state.value.confirmPassword) {
            sendNewEffect(
                SetNewPasswordScreenUIEffect.ShowSnackBarError(
                    Res.string.error_password_mismatch
                )
            )
            return
        }

        updateState { copy(isLoading = true) }
        tryToExecute(
            function = { onResetPassword() },
            onSuccess = { onResetPasswordSuccess() },
            onError = ::onResetPasswordError,
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

    private fun onResetPasswordError(throwable: Throwable) {
        updateState { copy(isLoading = false) }
        sendNewEffect(
            SetNewPasswordScreenUIEffect.ShowSnackBarError(
                errorStringResource = mapErrorMessage(throwable)
            )
        )
    }

    private fun checkResetButtonEnabled() {
        updateState {
            val isPasswordsMatch = passwordValidator.isPasswordMatch(newPassword, confirmPassword)
            val isPasswordSecure = passwordValidator.isValid(newPassword)

            copy(
                newPasswordErrorMessage = if (!isPasswordSecure)
                    Res.string.error_password_validation
                else null,
                isResetEnabled = isPasswordsMatch && isPasswordSecure
            )
        }
    }

    private fun mapErrorMessage(throwable: Throwable): StringResource {
        return when (throwable) {
            is AuthenticationException -> mapAuthenticationErrorToMessage(
                handlerSetNewPasswordException(throwable)
            )

            else -> mapErrorToMessage(ErrorState.GenericError(throwable))
        }
    }
}