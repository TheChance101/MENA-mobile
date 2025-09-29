@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


data class ChatScreenState(
    val chat: ChatUiState = ChatUiState(),
    val inputMessage: String = "",
    val chatListItems: List<ChatListItem> = emptyList(),
    val uiMessages: List<MessageUiState> = emptyList(),

    val userId: Uuid = Uuid.parse("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"), // temp until login

    val isResendMessageDialogVisible: Boolean = false,

    val failedMessageToReSend: MessageUiState? = null
)
