@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import kotlinx.datetime.LocalDateTime
import net.thechance.mena.core_chat.domain.entity.MessageStatus
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

    val failedMessageToReSend: MessageUiState? = null
)

sealed interface ChatListItem {
    data class DateSeparator(val label: UiText) : ChatListItem
    data class Message(val data: MessageUiState) : ChatListItem
}

data class MessageUiState(
    val id: Uuid = Uuid.random(),
    val senderId: Uuid = Uuid.random(),
    val chatId: Uuid = Uuid.random(),
    val sendTime: LocalDateTime = LocalDateTime.now(),
    val status: MessageStatus = MessageStatus.LOADING,
    val isMine: Boolean = true,
    val isLastInSeries: Boolean = false,
    val isVisibleMessageInfo: Boolean = false,
    val content: MessageContent
)

sealed class MessageContent{
    data class Text(val text: String): MessageContent()
    data class ImageUrl(val imageUrls: List<String>): MessageContent()
    data class ImageByteArray(val images: List<ByteArray>): MessageContent()
}
