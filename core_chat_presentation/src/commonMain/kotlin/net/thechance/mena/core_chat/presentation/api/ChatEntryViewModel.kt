package net.thechance.mena.core_chat.presentation.api

import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.presentation.shared.BaseViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ChatEntryViewModel(private val chatRepository: ChatRepository) :
    BaseViewModel<ChatEntryState, Unit>(ChatEntryState()) {
    fun getChatByUserId(userId: String) {
        tryToExecute(
            execute = {
                updateState { it.copy(isContentVisible = false) }
                chatRepository.getChatByContactUserId(Uuid.parse(userId))
            },
            onSuccess = { chat -> updateState { it.copy(chatId = chat.id, chatName = chat.name, isContentVisible = true) } },
            onError = { chat -> updateState { it.copy(isContentVisible = true) } }
        )
    }
}