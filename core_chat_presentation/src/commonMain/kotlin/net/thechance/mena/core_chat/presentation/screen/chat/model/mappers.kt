@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat.model

import net.thechance.mena.core_chat.domain.entity.Message
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import net.thechance.mena.core_chat.domain.entity.MessageStatus as DomainMessageStatus


fun Message.toUi(currentUserId: Uuid): TextMessageUiState {
    return TextMessageUiState(
        id = id,
        senderId = senderId,
        chatId = chatId,
        sendTime = sendAt,
        status = status.toUi(),
        isMine = senderId == currentUserId,
        text = text
    )
}

fun TextMessageUiState.toEntity(): Message {
    return Message(
        id = id,
        senderId = senderId,
        chatId = chatId,
        text = text,
        sendAt = sendTime,
        status = status.toEntity()
    )
}


private fun DomainMessageStatus.toUi(): MessageStatusUiState {
    return when (this) {
        DomainMessageStatus.LOADING -> MessageStatusUiState.SENDING
        DomainMessageStatus.SENT -> MessageStatusUiState.SENT
        DomainMessageStatus.READ -> MessageStatusUiState.READ
        DomainMessageStatus.FAILED -> MessageStatusUiState.FAILED
    }
}

private fun MessageStatusUiState.toEntity(): DomainMessageStatus {
    return when (this) {
        MessageStatusUiState.SENDING -> DomainMessageStatus.LOADING
        MessageStatusUiState.SENT -> DomainMessageStatus.SENT
        MessageStatusUiState.READ -> DomainMessageStatus.READ
        MessageStatusUiState.FAILED -> DomainMessageStatus.FAILED
    }
}