package net.thechance.mena.identity.presentation.screen.register.accountCreated

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.model.AuthenticationTokens
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import net.thechance.mena.identity.domain.repository.RegistrationDraftRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel

class AccountCreatedViewModel(
    private val authenticationRepository: AuthenticationRepository,
    private val registrationDraftRepository: RegistrationDraftRepository,
    private val authTokens: AuthenticationTokens? = null,
    private val phoneNumber: PhoneNumber? = null,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseScreenModel<AccountCreatedUIState, AccountCreatedUIEffect>
    (AccountCreatedUIState),
    AccountCreatedInteractionListener {

    override fun onClickGoToHome() {
        authTokens?.let { tokens ->
            completeRegistration(tokens)
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
        phoneNumber?.let { phone ->
            registrationDraftRepository.clearDraft(phone)
        }
    }
}