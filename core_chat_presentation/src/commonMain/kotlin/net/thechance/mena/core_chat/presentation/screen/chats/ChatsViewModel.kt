package net.thechance.mena.core_chat.presentation.screen.chats

import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.presentation.shared.BaseViewModel

class ChatsViewModel(
    private val contactsRepository: ContactsRepository
) : BaseViewModel<ChatsUiState, ChatsScreenEffect>(ChatsUiState()) {

    fun onNewChatClicked() {
        tryToExecute(
            onStart = { updateState { it.copy(isLoading = false) } },
            execute = { contactsRepository.getUserSyncedState() },
            onSuccess = { isSynced ->
                updateState { it.copy(isSynced = isSynced, isLoading = false) }
                if (isSynced) {
                    emitEffect(ChatsScreenEffect.NavigateToContacts)
                } else {
                    emitEffect(ChatsScreenEffect.NavigateToSyncContacts)
                }
            }
        )
    }
}


