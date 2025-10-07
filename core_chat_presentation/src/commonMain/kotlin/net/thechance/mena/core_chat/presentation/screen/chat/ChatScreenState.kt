@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import kotlinx.datetime.LocalDateTime
import net.thechance.mena.core_chat.presentation.utils.UiText
import net.thechance.mena.core_chat.presentation.utils.now
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class ChatScreenState(
    val chatId: Uuid? = null,
    val chatName: String = "",
    val chatAvatarUrl: String = "",
    val chatRequesterId: Uuid? = null,
    val inputMessage: String = "",
    val chatListItems: List<ChatListItem> = emptyList(),

    val isResendMessageDialogVisible: Boolean = false,

    val failedMessageToReSend: TextMessageUiState? = null
)

sealed interface ChatListItem {
    data class DateSeparator(val label: UiText) : ChatListItem
    data class Message(val data: MarkedMessageUiState) : ChatListItem
}

data class TextMessageUiState(
    val id: Uuid = Uuid.random(),
    val senderId: Uuid = Uuid.random(),
    val chatId: Uuid = Uuid.random(),
    val sendTime: LocalDateTime = LocalDateTime.now(),
    val status: MessageStatusUiState = MessageStatusUiState.SENDING,
    val isMine: Boolean = true,
    val text: String = ""
)

enum class MessageStatusUiState {
    SENDING,
    SENT,
    READ,
    FAILED
}

data class MarkedMessageUiState(
    val message: TextMessageUiState,
    val isMarkedLastInSeries: Boolean,
    val showMessageInfo: Boolean = false
)
