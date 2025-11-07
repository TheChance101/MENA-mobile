package net.thechance.mena.identity.presentation.screen.register.enterName

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.model.RegistrationDraft
import net.thechance.mena.identity.domain.repository.RegisterRepository
import net.thechance.mena.identity.domain.repository.RegistrationDraftRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.error.ErrorState
import net.thechance.mena.identity.presentation.base.error.handleAuthenticationException
import net.thechance.mena.identity.presentation.mapper.mapAuthenticationErrorToMessage
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import org.jetbrains.compose.resources.StringResource

class EnterNameViewModel(
    private val registerRepository: RegisterRepository,
    private val registrationDraftRepository: RegistrationDraftRepository,
    private val phoneNumber: PhoneNumber,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseScreenModel<EnterNameUIState, EnterNameUIEffect>(EnterNameUIState()),
    EnterNameInteractionListener {

    init {
        loadSavedData()
    }

    override fun onChangeFirstName(name: String) {
        updateState { copy(firstName = name) }
        updateNextButtonState()
        saveDraft { it.copy(firstName = name) }
    }

    override fun onLastNameChange(name: String) {
        updateState { copy(lastName = name) }
        updateNextButtonState()
        saveDraft { it.copy(lastName = name) }
    }

    override fun onUsernameChange(username: String) {
        updateState { copy(username = username, usernameError = null) }
        updateNextButtonState()
        saveDraft { it.copy(username = username) }
    }

    override fun onClickNext() {
        if (!state.value.isValidInput()) return
        checkUsernameAvailability()
    }

    override fun onClearErrorMessage() {
        updateState { copy(errorMessage = null) }
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
        savedDraft?.let { draft ->
            updateState {
                copy(
                    firstName = draft.firstName,
                    lastName = draft.lastName,
                    username = draft.username
                )
            }
            updateNextButtonState()
        }
    }

    private fun saveDraft(update: (RegistrationDraft) -> RegistrationDraft) {
        tryToExecute(
            function = {
                val draft = registrationDraftRepository.getDraft(phoneNumber) ?: RegistrationDraft()
                registrationDraftRepository.saveDraft(phoneNumber, update(draft))
            },
            onSuccess = {},
            onError = {},
            dispatcher = dispatcher
        )
    }

    private fun updateNextButtonState() {
        updateState { copy(isNextEnabled = state.value.isValidInput()) }
    }

    private fun checkUsernameAvailability() {
        updateState { copy(isLoading = true, isCheckingUsername = true, errorMessage = null) }
        tryToExecute(
            function = { registerRepository.checkUserExistence(state.value.username) },
            onSuccess = { onUsernameCheckSuccess() },
            onError = ::onUsernameCheckError,
            dispatcher = dispatcher
        )
    }

    private fun onUsernameCheckSuccess() {
        updateState { copy(isLoading = false, isCheckingUsername = false) }
        navigateToPasswordScreen()
    }

    private fun navigateToPasswordScreen() {
        sendNewEffect(createNavigateToPasswordEffect())
    }

    private fun createNavigateToPasswordEffect() = EnterNameUIEffect.NavigateToPassword(
        phoneNumber = phoneNumber,
        firstName = state.value.firstName,
        lastName = state.value.lastName,
        username = state.value.username
    )

    private fun onUsernameCheckError(throwable: Throwable) {
        updateState {
            copy(
                isLoading = false,
                isCheckingUsername = false,
                errorMessage = mapErrorMessage(throwable)
            )
        }
    }

    private fun mapErrorMessage(throwable: Throwable): StringResource = when (throwable) {
        is AuthenticationException -> mapAuthenticationErrorToMessage(
            handleAuthenticationException(throwable)
        )
        else -> mapErrorToMessage(ErrorState.GenericError(throwable))
    }
}