package net.thechance.mena.identity.presentation.screen.register.createPassword

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error_password_mismatch
import mena.identity_presentation.generated.resources.error_password_validation
import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.repository.RegisterRepository
import net.thechance.mena.identity.domain.useCase.validation.mobileNumber.PasswordValidator
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.error.ErrorState
import net.thechance.mena.identity.presentation.base.error.handleAuthenticationException
import net.thechance.mena.identity.presentation.mapper.mapAuthenticationErrorToMessage
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import net.thechance.mena.identity.presentation.util.validatePasswordConfirmation
import org.jetbrains.compose.resources.StringResource

class CreatePasswordViewModel(
    private val passwordValidator: PasswordValidator,
    private val registerRepository: RegisterRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseScreenModel<CreatePasswordUIState, CreatePasswordUIEffect>(
    CreatePasswordUIState()
), CreatePasswordInteractionListener {

    override fun onChangeNewPassword(password: String) {
        updateState { copy(newPassword = password) }
        checkCreateButtonEnabled()
    }

    override fun onChangeConfirmPassword(password: String) {
        updateState {
            copy(
                confirmPassword = password,
                confirmPasswordErrorMessage = validatePasswordConfirmation(newPassword, password),
            )
        }
        checkCreateButtonEnabled()
    }

    override fun onToggleNewPasswordVisibility() {
        updateState { copy(isNewPasswordVisible = !isNewPasswordVisible) }
    }

    override fun onToggleConfirmPasswordVisibility() {
        updateState { copy(isConfirmPasswordVisible = !isConfirmPasswordVisible) }
    }

    override fun onClickBack() {
        sendNewEffect(CreatePasswordUIEffect.NavigateBack)
    }

    override fun onClearErrorMessage() {
        updateState { copy(errorMessage = null) }
    }

    override fun onClickCreatePassword() {
        if (state.value.newPassword != state.value.confirmPassword) {
            updateState { copy(errorMessage = Res.string.error_password_mismatch) }
            return
        }

        updateState { copy(isLoading = true, errorMessage = null) }
        tryToExecute(
            function = ::onCreatePassword,
            onSuccess = { onCreatePasswordSuccess() },
            onError = ::onCreatePasswordError,
            dispatcher = dispatcher
        )
    }

    private suspend fun onCreatePassword() {
        // TODO: Uncomment when ready to integrate with backend
        // registerRepository.createPassword(
        //     state.value.confirmPassword,
        //     state.value.newPassword,
        // )

        // Bypass for UI testing
        delay(1000) // Simulate network delay
    }

    private fun onCreatePasswordSuccess() {
        updateState { copy(isLoading = false) }
        // TODO: Navigate to next screen
    }

    private fun onCreatePasswordError(throwable: Throwable) {
        updateState { copy(isLoading = false, errorMessage = mapErrorMessage(throwable)) }
    }

    private fun checkCreateButtonEnabled() {
        updateState {
            val isPasswordsMatch = newPassword.isNotBlank() && newPassword == confirmPassword
            val isPasswordSecure = passwordValidator.isValid(newPassword)

            copy(
                newPasswordErrorMessage = if (!isPasswordSecure)
                    Res.string.error_password_validation
                else null,
                isCreateEnabled = isPasswordsMatch && isPasswordSecure
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
}