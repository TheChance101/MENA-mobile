package net.thechance.mena.identity.presentation.screen.changePassword

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.changed_password_successfully
import mena.identity_presentation.generated.resources.error_password_mismatch
import mena.identity_presentation.generated.resources.error_password_validation
import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.exception.UnAuthorizedException
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.identity.domain.useCase.validation.mobileNumber.PasswordValidator
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.errorState.ErrorState
import net.thechance.mena.identity.presentation.mapper.mapAuthenticationErrorToMessage
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import org.jetbrains.compose.resources.StringResource

class ChangePasswordScreenViewModel(
    private val userRepository: UserRepository,
    private val passwordValidator: PasswordValidator,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseScreenModel<ChangePasswordScreenUIState, ChangePasswordScreenUIEffect>(
    initialState = ChangePasswordScreenUIState()
), ChangePasswordScreenInteractionListener {

    override fun onClickBack() {
        if (state.value.currentPage.index == PasswordPage.NEW_PASSWORD.index)
            updateState {
                copy(
                    currentPage = PasswordPage.CURRENT_PASSWORD
                )
            }
        else
            sendNewEffect(ChangePasswordScreenUIEffect.NavigateBack())
    }

    override fun onClickContinue() {
        updateState {
            copy(
                currentPage = PasswordPage.NEW_PASSWORD

            )
        }
    }

    override fun onClickSave() {
        updateState { copy(isLoading = true) }
        tryToExecute(
            function = ::changePassword,
            onSuccess = { onChangePasswordSuccess() },
            onError = ::onChangePasswordError,
            dispatcher = dispatcher
        )

    }

    override fun onChangeCurrentPassword(newValue: String) {
        updateState {
            copy(
                currentPasswordUIState = currentPasswordUIState.copy(
                    currentPassword = newValue,
                )
            )
        }
        updateContinueEnabledState()
    }

    override fun onChangeNewPassword(newValue: String) {

        updateState {
            copy(
                newPasswordUIState = newPasswordUIState.copy(
                    newPassword = newValue
                )
            )
        }
        updateSaveEnabledState()
    }

    override fun onChangeConfirmPassword(newValue: String) {
        val password = state.value.newPasswordUIState.newPassword
        val isPasswordMatch = passwordValidator.isPasswordMatch(password, newValue)

        updateState {
            copy(
                newPasswordUIState = newPasswordUIState.copy(
                    confirmPassword = newValue,
                    confirmPasswordErrorMessage = if (!isPasswordMatch)
                        Res.string.error_password_mismatch
                    else null
                )
            )
        }
        updateSaveEnabledState()
    }

    override fun onToggleCurrentPasswordVisibility() {
        updateState {
            copy(
                currentPasswordUIState = currentPasswordUIState.copy(
                    isCurrentPasswordVisible = !currentPasswordUIState.isCurrentPasswordVisible
                )
            )
        }
    }

    override fun onToggleNewPasswordVisibility() {
        updateState {
            copy(
                newPasswordUIState = newPasswordUIState.copy(
                    isNewPasswordVisible = !newPasswordUIState.isNewPasswordVisible
                )
            )
        }
    }

    override fun onToggleConfirmPasswordVisibility() {
        updateState {
            copy(
                newPasswordUIState = newPasswordUIState.copy(
                    isConfirmPasswordVisible = !newPasswordUIState.isConfirmPasswordVisible
                )
            )
        }
    }

    private suspend fun changePassword() {
        val currentPassword = state.value.currentPasswordUIState.currentPassword
        val newPassword = state.value.newPasswordUIState.newPassword
        val confirmPassword = state.value.newPasswordUIState.confirmPassword

        userRepository.changePassword(
            currentPassword = currentPassword,
            newPassword = newPassword,
            confirmPassword = confirmPassword
        )
    }

    private fun onChangePasswordSuccess() {
        updateState { copy(isLoading = false) }
        sendNewEffect(
            ChangePasswordScreenUIEffect.NavigateBack(
                successStringResource = Res.string.changed_password_successfully
            )
        )
    }

    private fun onChangePasswordError(throwable: Throwable) {
        updateState {
            copy(
                isLoading = false,
                currentPage = getPageAfterError(throwable)
            )
        }
        sendNewEffect(
            ChangePasswordScreenUIEffect.ShowSnackBarError(
                errorStringResource = mapErrorMessage(throwable)
            )
        )
    }

    private fun updateSaveEnabledState() {
        val newPassword = state.value.newPasswordUIState.newPassword
        val confirmPassword = state.value.newPasswordUIState.confirmPassword

        val isPasswordSecure = passwordValidator.isValid(newPassword)
        val isPasswordMatch = passwordValidator.isPasswordMatch(
            newPassword,
            confirmPassword
        )

        updateState {
            copy(
                newPasswordUIState = newPasswordUIState.copy(
                    newPasswordErrorMessage = if (!isPasswordSecure)
                        Res.string.error_password_validation
                    else null,
                    isSaveEnabled = isPasswordMatch && isPasswordSecure
                )
            )
        }
    }

    private fun updateContinueEnabledState() {
        val currentPassword = state.value.currentPasswordUIState.currentPassword
        val isPasswordValid = passwordValidator.isValid(currentPassword)
        updateState {
            copy(
                currentPasswordUIState = currentPasswordUIState.copy(
                    isContinueEnabled = isPasswordValid,
                    currentPasswordErrorMessage = if (!isPasswordValid)
                        Res.string.error_password_validation
                    else null
                )
            )
        }
    }

    private fun getPageAfterError(throwable: Throwable): PasswordPage {
        return if (throwable is UnAuthorizedException)
            PasswordPage.CURRENT_PASSWORD
        else
            PasswordPage.NEW_PASSWORD
    }


    private fun mapErrorMessage(throwable: Throwable): StringResource {
        return when (throwable) {
            is AuthenticationException -> mapAuthenticationErrorToMessage(
                handleChangePasswordException(throwable)
            )

            else -> mapErrorToMessage(ErrorState.GenericError(throwable))
        }
    }
}