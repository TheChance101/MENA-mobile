package net.thechance.mena.identity.presentation.screen.register.selectGender

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.exception.InvalidRequestException
import net.thechance.mena.identity.domain.model.AuthenticationTokens
import net.thechance.mena.identity.domain.model.RegistrationDraft
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import net.thechance.mena.identity.domain.repository.RegisterRepository
import net.thechance.mena.identity.domain.repository.RegistrationDraftRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.errorState.ErrorState
import net.thechance.mena.identity.presentation.mapper.mapAuthenticationErrorToMessage
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import net.thechance.mena.identity.presentation.screen.register.shared.AuthUIState
import net.thechance.mena.identity.presentation.screen.register.shared.toAuthUIState
import net.thechance.mena.identity.presentation.screen.register.shared.RegisterUIState
import net.thechance.mena.identity.presentation.screen.register.shared.toPhoneNumber
import net.thechance.mena.identity.presentation.screen.register.shared.toRegisterRequest
import org.jetbrains.compose.resources.StringResource

class SelectGenderScreenViewModel(
    private val registerRepository: RegisterRepository,
    private val registrationDraftRepository: RegistrationDraftRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val registerUIState: RegisterUIState,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    BaseScreenModel<SelectGenderScreenUIState, SelectGenderScreenUIEffect>(
        SelectGenderScreenUIState()
    ), SelectGenderScreenInteractionListener {

    init {
        loadSavedData()
    }

    override fun onClickRegister() {
        state.value.gender?.let { gender -> startRegistration(gender) }
    }

    override fun onChangeGender(gender: Gender) {
        updateState { copy(gender = gender, isRegisterEnabled = true) }
        saveGender(gender)
    }

    override fun onClickOkSessionExpired() {
        sendNewEffect(SelectGenderScreenUIEffect.NavigateBackToRegister)
    }

    private fun loadSavedData() {
        tryToExecute(
            function = { registrationDraftRepository.getDraft(registerUIState.phoneNumber.toPhoneNumber()) },
            onSuccess = ::handleSavedDraft,
            dispatcher = dispatcher
        )
    }

    private fun handleSavedDraft(savedDraft: RegistrationDraft?) {
        savedDraft?.gender?.let { gender ->
            updateState { copy(gender = gender, isRegisterEnabled = true) }
        }
    }

    private fun startRegistration(gender: Gender) {
        updateState { copy(isRegisterLoading = true) }
        tryToExecute(
            function = { register(gender) },
            onSuccess = ::onRegisterSuccess,
            onError = ::onRegisterError,
            dispatcher = dispatcher
        )
    }

    private suspend fun register(gender: Gender): AuthenticationTokens =
        registerRepository.register(registerUIState.toRegisterRequest(gender))


    private fun onRegisterSuccess(authTokens: AuthenticationTokens) {
        updateState { copy(isRegisterLoading = false) }
        saveTokensTemporarily(authTokens)
        clearDraft()
        navigateToUploadScreen(authTokens)
    }

    private fun navigateToUploadScreen(authTokens: AuthenticationTokens) {
        sendNewEffect(
            SelectGenderScreenUIEffect.NavigateToUploadProfileImage(
                AuthUIState(
                    authTokens = authTokens.toAuthUIState(),
                    phoneNumber = registerUIState.phoneNumber
                )
            )
        )
    }

    private fun saveTokensTemporarily(authTokens: AuthenticationTokens) {
        tryToExecute(
            function = { authenticationRepository.saveAuthTokensWithoutEmit(authTokens) },
            dispatcher = dispatcher
        )
    }

    private fun clearDraft() {
        tryToExecute(
            function = { registrationDraftRepository.clearDraft(registerUIState.phoneNumber.toPhoneNumber()) },
            dispatcher = dispatcher
        )
    }

    private fun onRegisterError(throwable: Throwable) {
        updateState { copy(isRegisterLoading = false,) }
        when(throwable){
            is InvalidRequestException -> {
                updateState { copy(showSessionExpiredDialog = true) }
            }

            else ->{
                sendNewEffect(
                    SelectGenderScreenUIEffect.ShowSnackBarError(
                        errorStringResource = mapErrorMessage(throwable)
                    )
                )
            }
        }
    }

    private fun saveGender(gender: Gender) {
        tryToExecute(
            function = {
                val draft = registrationDraftRepository.getDraft(registerUIState.phoneNumber.toPhoneNumber())
                            ?: RegistrationDraft()
                registrationDraftRepository.saveDraft(
                    registerUIState.phoneNumber.toPhoneNumber(),
                    draft.copy(gender = gender)
                )
            },
            dispatcher = dispatcher
        )
    }

    private fun mapErrorMessage(throwable: Throwable): StringResource = when (throwable) {
        is AuthenticationException -> mapAuthenticationErrorToMessage(
            handleSelectGenderException(throwable)
        )

        else -> mapErrorToMessage(ErrorState.GenericError(throwable))
    }
}