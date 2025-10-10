package net.thechance.mena.core_chat.presentation.screen.home

import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.presentation.navigation.ChatEffector
import net.thechance.mena.core_chat.presentation.navigation.ContactsRoute
import net.thechance.mena.core_chat.presentation.navigation.SyncContactsRoute
import net.thechance.mena.core_chat.presentation.navigation.WalletRoute
import net.thechance.mena.core_chat.presentation.shared.BaseViewModel

class HomeViewModel(
    private val contactsRepository: ContactsRepository,
    effector: ChatEffector
) : BaseViewModel<HomeScreenState>(HomeScreenState(), effector) {

    fun onNewChatClicked() {
        tryToExecute(
            onStart = { updateState { it.copy(isLoading = false) } },
            execute = { contactsRepository.getSyncStatus() },
            onSuccess = { isSynced ->
                updateState { it.copy(isSynced = isSynced, isLoading = false) }
                if (isSynced) {
                    navigate(ContactsRoute)
                } else {
                    navigate(SyncContactsRoute(forceSync = false))
                }
            }
        )
    }

    fun onWalletClicked() {
        navigate(WalletRoute)
    }
}


