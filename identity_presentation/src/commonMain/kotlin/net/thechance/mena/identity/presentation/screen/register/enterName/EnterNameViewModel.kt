package net.thechance.mena.identity.presentation.screen.register.enterName

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.model.RegistrationDraft
import net.thechance.mena.identity.domain.repository.RegisterRepository
import net.thechance.mena.identity.domain.repository.RegistrationDraftRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.errorState.ErrorState
import net.thechance.mena.identity.presentation.mapper.mapAuthenticationErrorToMessage
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import net.thechance.mena.identity.presentation.screen.register.shared.RegisterUIState
import net.thechance.mena.identity.presentation.screen.register.shared.toPhoneNumber
import org.jetbrains.compose.resources.StringResource

class EnterNameViewModel(
    private val registerRepository: RegisterRepository,
    private val registrationDraftRepository: RegistrationDraftRepository,
    private val registerUIState: RegisterUIState,
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

    private fun loadSavedData() {
        tryToExecute(
            function = { registrationDraftRepository.getDraft(registerUIState.phoneNumber.toPhoneNumber()) },
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
                val draft = registrationDraftRepository.getDraft(registerUIState.phoneNumber.toPhoneNumber()) ?: RegistrationDraft()
                registrationDraftRepository.saveDraft(registerUIState.phoneNumber.toPhoneNumber(), update(draft))
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
        updateState { copy(isLoading = true, isCheckingUsername = true) }
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
        registerUIState = RegisterUIState(
            phoneNumber = registerUIState.phoneNumber,
            firstName = state.value.firstName,
            lastName = state.value.lastName,
            username = state.value.username
        )
    )

    private fun onUsernameCheckError(throwable: Throwable) {
        updateState {
            copy(
                isLoading = false,
                isCheckingUsername = false,
            )
        }
        sendNewEffect(
            EnterNameUIEffect.ShowSnackBarError(
                errorStringResource = mapErrorMessage(throwable)
            )
        )
    }

    private fun mapErrorMessage(throwable: Throwable): StringResource = when (throwable) {
        is AuthenticationException -> mapAuthenticationErrorToMessage(
            handleRegisterEnterNameException(throwable)
        )

        else -> mapErrorToMessage(ErrorState.GenericError(throwable))
    }
}