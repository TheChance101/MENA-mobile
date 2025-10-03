@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package net.thechance.mena.core_chat.data.chat

import net.thechance.mena.core_chat.data.chat.dto.ChatDto
import net.thechance.mena.core_chat.data.chat.dto.MessageDto
import net.thechance.mena.core_chat.data.chat.dto.SendMessageDto
import net.thechance.mena.core_chat.data.chat.utils.toInstant
import net.thechance.mena.core_chat.data.chat.utils.toLocalDateTime
import net.thechance.mena.core_chat.domain.entity.Chat
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


fun MessageDto.toDomain() = Message(
    id = Uuid.parse(id),
    senderId = Uuid.parse(senderId),
    chatId = Uuid.parse(chatId),
    text = text,
    sendAt = Instant.parse(sendAt).toLocalDateTime(),
    status = if (isRead) MessageStatus.READ else MessageStatus.SENT
)

fun ChatDto.toDomain() = Chat(
    id = Uuid.parse(id),
    imageUrl = imageUrl,
    name = name,
    requesterId = Uuid.parse(requesterId)
)

fun Message.toDto() = MessageDto(
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