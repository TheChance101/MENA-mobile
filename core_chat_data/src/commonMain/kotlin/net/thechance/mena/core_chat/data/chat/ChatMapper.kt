@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package net.thechance.mena.core_chat.data.chat

import net.thechance.mena.core_chat.data.chat.dto.ChatDto
import net.thechance.mena.core_chat.data.chat.dto.MessageDto
import net.thechance.mena.core_chat.data.chat.dto.SendMessageDto
import net.thechance.mena.core_chat.data.chat.utils.toInstant
import net.thechance.mena.core_chat.data.chat.utils.toLocalDateTime
import net.thechance.mena.core_chat.data.contacts.utils.getUuidOrNull
import net.thechance.mena.core_chat.domain.entity.Chat
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi


fun MessageDto.toDomain(): Message? {
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