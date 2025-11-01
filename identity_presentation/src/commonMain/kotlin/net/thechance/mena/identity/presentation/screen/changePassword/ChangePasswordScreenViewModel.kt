package net.thechance.mena.identity.presentation.screen.changePassword

import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.changed_password_successfully
import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.error.ErrorState
import net.thechance.mena.identity.presentation.base.error.handleAuthenticationException
import net.thechance.mena.identity.presentation.mapper.mapAuthenticationErrorToMessage
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.SnackBarType
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.SnackBarUiState
import net.thechance.mena.identity.presentation.util.isPasswordValid
import org.jetbrains.compose.resources.StringResource

class ChangePasswordScreenViewModel(
    private val userRepository: UserRepository
) : BaseScreenModel<ChangePasswordScreenUIState, ChangePasswordScreenUIEffect>(
    initialState = ChangePasswordScreenUIState()
), ChangePasswordScreenInteractionListener {
    override fun onClickBack() {
        if (state.value.currentPage == 1)
            updateState { copy(currentPage = 0) }
        else
            sendNewEffect(ChangePasswordScreenUIEffect.NavigateBack())
    }

    override fun onClickContinue() {
        updateState { copy(currentPage = 1) }
    }

    override fun onClickSave() {
        updateState { copy(isLoading = true) }
        tryToExecute(
            function = ::changePassword,
            onSuccess = { onChangePasswordSuccess() },
            onError = ::onChangePasswordError
        )

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
                snackBarUiState = SnackBarUiState(
                    message = Res.string.changed_password_successfully,
                    snackBarType = SnackBarType.SUCCESS,
                    isVisible = true
                )
            )
        )
    }

    private fun onChangePasswordError(throwable: Throwable) {
        updateState { copy(errorMessage = mapErrorMessage(throwable), isLoading = false) }
    }

    override fun onChangeCurrentPassword(newValue: String) {
        updateState {
            copy(
                currentPasswordUIState = currentPasswordUIState.copy(
                    currentPassword = newValue
                )
            )
        }
        isContinueEnabled()
    }

    override fun onChangeNewPassword(newValue: String) {
        updateState {
            copy(
                newPasswordUIState = newPasswordUIState.copy(
                    newPassword = newValue
                )
            )
        }
        isSaveEnabled()
    }

    override fun onChangeConfirmPassword(newValue: String) {
        updateState {
            copy(
                newPasswordUIState = newPasswordUIState.copy(
                    confirmPassword = newValue
                )
            )
        }
        isSaveEnabled()
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

    private fun isSaveEnabled() {
        updateState {
            copy(
                newPasswordUIState = newPasswordUIState.copy(
                    isSaveEnabled = isPasswordValid(newPasswordUIState.newPassword) &&
                            (newPasswordUIState.newPassword == newPasswordUIState.confirmPassword)
                )
            )
        }
    }

    private fun isContinueEnabled() {
        updateState {
            copy(
                currentPasswordUIState = currentPasswordUIState.copy(
                    isContinueEnabled = isPasswordValid(currentPasswordUIState.currentPassword)
                )
            )
        }
    }

    private fun mapErrorMessage(throwable: Throwable): StringResource {
        return when (throwable) {
            is AuthenticationException -> mapAuthenticationErrorToMessage(
                handleAuthenticationException(throwable)
            )

            else -> mapErrorToMessage(ErrorState.GenericError(throwable))
        }
    }

    fun onClearErrorMessage() {
        updateState { copy(errorMessage = null) }
    }
}