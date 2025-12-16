package net.thechance.mena.identity.presentation.screen.register.accountCreated

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.domain.model.AuthenticationTokens
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import net.thechance.mena.identity.domain.repository.RegistrationDraftRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.screen.register.shared.AuthUIState
import net.thechance.mena.identity.presentation.screen.register.shared.toAuthenticationTokens
import net.thechance.mena.identity.presentation.screen.register.shared.toPhoneNumber

class AccountCreatedViewModel(
    private val authenticationRepository: AuthenticationRepository,
    private val registrationDraftRepository: RegistrationDraftRepository,
    private val authUiState: AuthUIState,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseScreenModel<AccountCreatedUIState, AccountCreatedUIEffect>
    (AccountCreatedUIState),
    AccountCreatedInteractionListener {

    override fun onClickGoToHome() {
        authUiState.authTokens?.let { tokens ->
            completeRegistration(tokens.toAuthenticationTokens())
        }
    }

    private fun completeRegistration(tokens: AuthenticationTokens) {
        tryToExecute(
            function = {
                authenticationRepository.saveAuthTokensAndEmit(tokens)
                clearRegistrationData()
            },
            dispatcher = dispatcher
        )
    }

    private suspend fun clearRegistrationData() {
        registrationDraftRepository.clearLastPhoneNumber()
        authUiState.phoneNumber?.let { phone ->
            registrationDraftRepository.clearDraft(phone.toPhoneNumber())
        }
    }
}