@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import net.thechance.mena.core_chat.presentation.screen.chat.model.ChatListItem
import net.thechance.mena.core_chat.presentation.screen.chat.model.TextMessageUiState
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class ChatState(
    val chatId: Uuid? = null,
    val chatName: String = "",
    val chatAvatarUrl: String = "",
    val chatRequesterId: Uuid? = null,
    val inputMessage: String = "",
    val chatListItems: List<ChatListItem> = emptyList(),
    val uiMessages: List<TextMessageUiState> = emptyList(),

    val isResendMessageDialogVisible: Boolean = false,

    val failedMessageToReSend: TextMessageUiState? = null
)
