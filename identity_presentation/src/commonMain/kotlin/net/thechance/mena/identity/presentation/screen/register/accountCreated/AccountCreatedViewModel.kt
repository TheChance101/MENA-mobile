package net.thechance.mena.identity.presentation.screen.register.accountCreated

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.presentation.base.BaseScreenModel

class AccountCreatedViewModel(
    val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseScreenModel<AccountCreatedUIState, AccountCreatedUIEffect>
    (AccountCreatedUIState),
    AccountCreatedInteractionListener {

    override fun onClickGoToHome() {
        sendNewEffect(AccountCreatedUIEffect.NavigateToHome)
    }
}