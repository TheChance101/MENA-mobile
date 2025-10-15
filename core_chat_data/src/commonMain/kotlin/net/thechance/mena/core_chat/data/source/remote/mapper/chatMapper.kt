@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package net.thechance.mena.core_chat.data.source.remote.mapper

import net.thechance.mena.core_chat.data.source.local.database.MessageLocalDto
import net.thechance.mena.core_chat.data.source.remote.dto.ChatDto
import net.thechance.mena.core_chat.data.source.remote.dto.MessageDto
import net.thechance.mena.core_chat.data.utils.getUuidOrNull
import net.thechance.mena.core_chat.data.utils.toInstant
import net.thechance.mena.core_chat.data.utils.toLocalDateTime
import net.thechance.mena.core_chat.domain.entity.Chat
import net.thechance.mena.core_chat.domain.entity.ImagesSource
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


fun MessageDto.toDomain(): Message? {
    val content = when {
        !text.isNullOrBlank() -> MessageContent.Text(text)
        !images.isNullOrEmpty() -> MessageContent.Images(ImagesSource.Remote(images))
        else -> return null
    }

    return Message(
        id = getUuidOrNull(id) ?: return null,
        senderId = getUuidOrNull(senderId) ?: return null,
        chatId = getUuidOrNull(chatId) ?: return null,
        sendAt = Instant.parse(sendAt).toLocalDateTime(),
        status = if (isRead) MessageStatus.READ else MessageStatus.SENT,
        content = content
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

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
fun Message.toLocalDto(): MessageLocalDto {
    val content = this.content
    val text = if (content is MessageContent.Text) content.text else null
    val source = if (content is MessageContent.Images) content.source else null
    val images = if (source is ImagesSource.Local) source.byteArrays else null


    return MessageLocalDto(
        id = this.id.toString(),
        senderId = this.senderId.toString(),
        text = text,
        images = images,
        timestamp = this.sendAt.toInstant().toEpochMilliseconds(),
        chatId = this.chatId.toString(),
        status = status.toLocalDto()
    )
}


fun MessageLocalDto.toDomain(): Message {
    val content = if (text != null) {
        MessageContent.Text(text)
    } else if (images != null) {
        MessageContent.Images(ImagesSource.Local(images))
    } else {
        error("Invalid message content")
    }

    return Message(
        id = Uuid.parse(this.id),
        senderId = Uuid.parse(this.senderId),
        chatId = Uuid.parse(this.chatId),
        content = content,
        sendAt = Instant.fromEpochMilliseconds(this.timestamp).toLocalDateTime(),
        status = status.toDomain()
    )
}

fun MessageLocalDto.MessageStatus.toDomain(): MessageStatus {
    return when (this) {
        MessageLocalDto.MessageStatus.LOADING -> MessageStatus.LOADING
        MessageLocalDto.MessageStatus.SENT -> MessageStatus.SENT
        MessageLocalDto.MessageStatus.FAILED -> MessageStatus.FAILED
        MessageLocalDto.MessageStatus.READ -> MessageStatus.READ
    }
}

fun MessageStatus.toLocalDto(): MessageLocalDto.MessageStatus {
    return when (this) {
        MessageStatus.LOADING -> MessageLocalDto.MessageStatus.LOADING
        MessageStatus.SENT -> MessageLocalDto.MessageStatus.SENT
        MessageStatus.FAILED -> MessageLocalDto.MessageStatus.FAILED
        MessageStatus.READ -> MessageLocalDto.MessageStatus.READ
    }
}