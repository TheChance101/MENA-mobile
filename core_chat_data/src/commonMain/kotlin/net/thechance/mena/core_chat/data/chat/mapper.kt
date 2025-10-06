@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package net.thechance.mena.core_chat.data.chat

import net.thechance.mena.core_chat.data.chat.dto.ChatDto
import net.thechance.mena.core_chat.data.chat.dto.MessageRemoteDto
import net.thechance.mena.core_chat.data.chat.dto.SendMessageDto
import net.thechance.mena.core_chat.data.local_database.dto.MessageLocalDto
import net.thechance.mena.core_chat.data.utils.getUuidOrNull
import net.thechance.mena.core_chat.data.utils.toInstant
import net.thechance.mena.core_chat.data.utils.toLocalDateTime
import net.thechance.mena.core_chat.domain.entity.Chat
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


fun MessageRemoteDto.toDomain(): Message? {
    return Message(
        id = getUuidOrNull(id) ?: return null,
        senderId = getUuidOrNull(senderId) ?: return null,
        chatId = getUuidOrNull(chatId) ?: return null,
        text = text,
        sendAt = Instant.parse(sendAt).toLocalDateTime(),
        status = if (isRead) MessageStatus.READ else MessageStatus.SENT
    )
}

fun ChatDto.toDomain(): Chat? {
    return Chat(
        id = getUuidOrNull(id) ?: return null,
        imageUrl = imageUrl,
        name = name,
        requesterId = getUuidOrNull(requesterId) ?: return null
    )
}

fun Message.toDto() = MessageRemoteDto(
    id = id.toString(),
    senderId = senderId.toString(),
    chatId = chatId.toString(),
    text = text,
    sendAt = sendAt.toInstant().toString(),
    isRead = status == MessageStatus.READ
)

fun Message.toSendMessageRequestDto() = SendMessageDto(
    chatId = chatId.toString(),
    text = text
)

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
fun Message.toLocalDto(): MessageLocalDto {
    return MessageLocalDto(
        id = this.id.toString(),
        senderId = this.senderId.toString(),
        text = this.text,
        timestamp = this.sendAt.toInstant().toEpochMilliseconds(),
        chatId = this.chatId.toString(),
        status = when (status) {
            MessageStatus.LOADING -> MessageLocalDto.MessageStatus.SENDING
            MessageStatus.SENT -> MessageLocalDto.MessageStatus.SENT
            MessageStatus.FAILED -> MessageLocalDto.MessageStatus.FAILED
            MessageStatus.READ -> MessageLocalDto.MessageStatus.READ
        }
    )
}

fun MessageLocalDto.toEntity(): Message {
    return Message(
        id = Uuid.parse(this.id),
        senderId = Uuid.parse(this.senderId),
        chatId = Uuid.parse(this.chatId),
        text = this.text,
        sendAt = Instant.fromEpochMilliseconds(this.timestamp).toLocalDateTime(),
        status = when (this.status) {
            MessageLocalDto.MessageStatus.SENDING -> MessageStatus.LOADING
            MessageLocalDto.MessageStatus.SENT -> MessageStatus.SENT
            MessageLocalDto.MessageStatus.FAILED -> MessageStatus.FAILED
            MessageLocalDto.MessageStatus.READ -> MessageStatus.READ
        }
    )
}