package net.thechance.mena.core_chat.presentation.api

import net.thechance.mena.core_chat.domain.entity.Chat
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.presentation.shared.BaseViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ChatEntryViewModel(private val chatRepository: ChatRepository) :
    BaseViewModel<ChatEntryState, Unit>(ChatEntryState()) {
    fun getChatByUserId(userId: String) {
        tryToExecute(
            onStart = { updateState { it.copy(isContentVisible = false, error = false) } },
            execute = { chatRepository.getChatByOtherUserId(Uuid.parse(userId)) },
            onSuccess = ::onGetChatByOtherUserIdSuccess,
            onError = { onGetChatByOtherUserIdError() }
        )
    }

    private fun onGetChatByOtherUserIdError() {
        updateState { it.copy(isContentVisible = false, error = true) }
    }

    private fun onGetChatByOtherUserIdSuccess(chat: Chat) {
        updateState {
            it.copy(
                chatId = chat.id,
                chatName = chat.name,
                isContentVisible = true,
                error = false
            )
        }
    }
}