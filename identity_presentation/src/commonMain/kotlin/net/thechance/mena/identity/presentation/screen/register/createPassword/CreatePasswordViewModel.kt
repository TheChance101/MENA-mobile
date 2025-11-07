package net.thechance.mena.identity.presentation.screen.register.createPassword

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error_password_mismatch
import mena.identity_presentation.generated.resources.error_password_validation
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.model.RegistrationDraft
import net.thechance.mena.identity.domain.repository.RegistrationDraftRepository
import net.thechance.mena.identity.domain.useCase.validation.mobileNumber.PasswordValidator
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.util.validatePasswordConfirmation

class CreatePasswordViewModel(
    private val passwordValidator: PasswordValidator,
    private val registrationDraftRepository: RegistrationDraftRepository,
    private val phoneNumber: PhoneNumber,
    private val firstName: String,
    private val lastName: String,
    private val username: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseScreenModel<CreatePasswordUIState, CreatePasswordUIEffect>(
    CreatePasswordUIState()
), CreatePasswordInteractionListener {

    init {
        loadSavedData()
    }

    override fun onChangeNewPassword(password: String) {
        updateState { copy(newPassword = password) }
        checkCreateButtonEnabled()
        savePasswordIfValid(password)
    }

    override fun onChangeConfirmPassword(password: String) {
        updateState {
            copy(
                confirmPassword = password,
                confirmPasswordErrorMessage = validatePasswordConfirmation(newPassword, password)
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

    override fun onClearErrorMessage() {
        updateState { copy(errorMessage = null) }
    }

    override fun onClickCreatePassword() {
        if (passwordsDoNotMatch()) {
            updateState { copy(errorMessage = Res.string.error_password_mismatch) }
            return
        }
        navigateToDatePicker()
    }

    private fun loadSavedData() {
        tryToExecute(
            function = { registrationDraftRepository.getDraft(phoneNumber) },
            onSuccess = ::handleSavedDraft,
            onError = {},
            dispatcher = dispatcher
        )
    }

    private fun handleSavedDraft(savedDraft: RegistrationDraft?) {
        savedDraft?.password?.takeIf { it.isNotBlank() }?.let { password ->
            updateState {
                copy(
                    newPassword = password,
                    confirmPassword = password
                )
            }
            checkCreateButtonEnabled()
        }
    }

    private fun savePasswordIfValid(password: String) {
        if (password.isNotBlank() && passwordValidator.isValid(password)) {
            savePassword(password)
        }
    }

    private fun savePassword(password: String) {
        tryToExecute(
            function = {
                val draft = registrationDraftRepository.getDraft(phoneNumber) ?: RegistrationDraft()
                registrationDraftRepository.saveDraft(phoneNumber, draft.copy(password = password))
            },
            onSuccess = {},
            onError = {},
            dispatcher = dispatcher
        )
    }

    private fun passwordsDoNotMatch(): Boolean =
        state.value.newPassword != state.value.confirmPassword

    private fun navigateToDatePicker() {
        sendNewEffect(createNavigateToDatePickerEffect())
    }

    private fun createNavigateToDatePickerEffect() = CreatePasswordUIEffect.NavigateToDatePicker(
        phoneNumber = phoneNumber,
        firstName = firstName,
        lastName = lastName,
        username = username,
        password = state.value.newPassword
    )

    private fun checkCreateButtonEnabled() {
        updateState {
            val passwordsMatch = newPassword.isNotBlank() && newPassword == confirmPassword
            val passwordSecure = passwordValidator.isValid(newPassword)

            copy(
                newPasswordErrorMessage = if (!passwordSecure) Res.string.error_password_validation else null,
                isCreateEnabled = passwordsMatch && passwordSecure
            )
        }
    }
}