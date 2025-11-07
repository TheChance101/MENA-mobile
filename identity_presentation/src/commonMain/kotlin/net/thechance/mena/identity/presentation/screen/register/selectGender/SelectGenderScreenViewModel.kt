package net.thechance.mena.identity.presentation.screen.register.selectGender

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.datetime.LocalDate
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.model.AuthenticationTokens
import net.thechance.mena.identity.domain.model.RegisterRequest
import net.thechance.mena.identity.domain.model.RegistrationDraft
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import net.thechance.mena.identity.domain.repository.RegisterRepository
import net.thechance.mena.identity.domain.repository.RegistrationDraftRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.error.ErrorState
import net.thechance.mena.identity.presentation.base.error.handleAuthenticationException
import net.thechance.mena.identity.presentation.mapper.mapAuthenticationErrorToMessage
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import org.jetbrains.compose.resources.StringResource

class SelectGenderScreenViewModel(
    private val registerRepository: RegisterRepository,
    private val registrationDraftRepository: RegistrationDraftRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val phoneNumber: PhoneNumber,
    private val firstName: String,
    private val lastName: String,
    private val username: String,
    private val password: String,
    private val birthDate: LocalDate,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    BaseScreenModel<SelectGenderScreenUIState, SelectGenderScreenUIEffect>(
        SelectGenderScreenUIState()
    ), SelectGenderScreenInteractionListener {

    init {
        loadSavedData()
    }

    override fun onClickRegister() {
        state.value.gender?.let { gender ->
            startRegistration(gender)
        }
    }

    override fun onChangeGender(gender: Gender) {
        updateState { copy(gender = gender, isRegisterEnabled = true) }
        saveGender(gender)
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
        savedDraft?.gender?.let { gender ->
            updateState { copy(gender = gender, isRegisterEnabled = true) }
        }
    }

    private fun startRegistration(gender: Gender) {
        updateState { copy(isRegisterLoading = true, errorMessage = null) }
        tryToExecute(
            function = { register(gender) },
            onSuccess = ::onRegisterSuccess,
            onError = ::onRegisterError,
            dispatcher = dispatcher
        )
    }

    private suspend fun register(gender: Gender): AuthenticationTokens =
        registerRepository.register(createRegisterRequest(gender))

    private fun createRegisterRequest(gender: Gender) = RegisterRequest(
        phoneNumber = phoneNumber,
        username = username,
        firstName = firstName,
        lastName = lastName,
        birthDate = birthDate,
        gender = gender,
        password = password
    )

    private fun onRegisterSuccess(authTokens: AuthenticationTokens) {
        updateState { copy(isRegisterLoading = false) }
        saveTokensTemporarily(authTokens)
        clearDraft()
        navigateToUploadScreen(authTokens)
    }

    private fun navigateToUploadScreen(authTokens: AuthenticationTokens) {
        sendNewEffect(
            SelectGenderScreenUIEffect.NavigateToUploadProfileImage(authTokens, phoneNumber)
        )
    }

    private fun saveTokensTemporarily(authTokens: AuthenticationTokens) {
        tryToExecute(
            function = { authenticationRepository.saveAuthTokensWithoutEmit(authTokens) },
            onSuccess = {},
            onError = {},
            dispatcher = dispatcher
        )
    }

    private fun clearDraft() {
        tryToExecute(
            function = { registrationDraftRepository.clearDraft(phoneNumber) },
            onSuccess = {},
            onError = {},
            dispatcher = dispatcher
        )
    }

    private fun onRegisterError(throwable: Throwable) {
        updateState {
            copy(
                isRegisterLoading = false,
                errorMessage = mapErrorMessage(throwable)
            )
        }
    }

    private fun saveGender(gender: Gender) {
        tryToExecute(
            function = {
                val draft = registrationDraftRepository.getDraft(phoneNumber) ?: RegistrationDraft()
                registrationDraftRepository.saveDraft(phoneNumber, draft.copy(gender = gender))
            },
            onSuccess = {},
            onError = {},
            dispatcher = dispatcher
        )
    }

    private fun mapErrorMessage(throwable: Throwable): StringResource = when (throwable) {
        is AuthenticationException -> mapAuthenticationErrorToMessage(
            handleAuthenticationException(throwable)
        )
        else -> mapErrorToMessage(ErrorState.GenericError(throwable))
    }
}