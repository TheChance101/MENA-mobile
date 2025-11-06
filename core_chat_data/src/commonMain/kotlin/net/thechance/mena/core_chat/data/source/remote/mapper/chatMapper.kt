@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package net.thechance.mena.core_chat.data.source.remote.mapper

import net.thechance.mena.core_chat.data.source.local.database.cachedChat.CachedChatLocalDto
import net.thechance.mena.core_chat.data.source.local.database.cachedMessage.CachedMessageLocalDto
import net.thechance.mena.core_chat.data.source.local.database.pendingMessage.PendingMessageLocalDto
import net.thechance.mena.core_chat.data.source.remote.dto.ChatDto
import net.thechance.mena.core_chat.data.source.remote.dto.MarkAsReadDto
import net.thechance.mena.core_chat.data.source.remote.dto.MessageDto
import net.thechance.mena.core_chat.data.source.remote.dto.MessageReactionDto
import net.thechance.mena.core_chat.data.source.remote.dto.PagedDataDto
import net.thechance.mena.core_chat.data.source.remote.dto.events.DeleteChatDto
import net.thechance.mena.core_chat.data.utils.getUuidOrNull
import net.thechance.mena.core_chat.data.utils.toInstant
import net.thechance.mena.core_chat.data.utils.toLocalDateTime
import net.thechance.mena.core_chat.domain.entity.Chat
import net.thechance.mena.core_chat.domain.entity.ImageData
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.domain.entity.MessageReaction
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.domain.event.DeleteChatEvent
import net.thechance.mena.core_chat.domain.event.MarkMessageAsReadEvent
import net.thechance.mena.core_chat.domain.model.PagedData
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


fun MessageDto.toDomain(): Message? {
    val content = when {
        !text.isNullOrBlank() -> MessageContent.Text(text)
        !imageUrl.isNullOrEmpty() -> MessageContent.Image(ImageData.ImageUrl(imageUrl))
        else -> return null
    }

    return Message(
        id = getUuidOrNull(id) ?: return null,
        senderId = getUuidOrNull(senderId) ?: return null,
        chatId = getUuidOrNull(chatId) ?: return null,
        sendAt = Instant.parse(sendAt).toLocalDateTime(),
        status = if (isRead) MessageStatus.READ else MessageStatus.SENT,
        content = content,
        reactions = reactions.map(MessageReactionDto::toDomain),
        isMine = isMine
    )
}

fun MessageReactionDto.toDomain(): MessageReaction {
    return MessageReaction(
        emoji = emoji,
        userId = getUuidOrNull(userId) ?: error("Invalid user ID"),
        messageId = getUuidOrNull(messageId) ?: error("Invalid message ID")
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

fun ChatDto.toLocalDto(): CachedChatLocalDto {
    return CachedChatLocalDto(
        id = id,
        imageUrl = imageUrl,
        name = name,
        requesterId = requesterId
    )
}

fun CachedChatLocalDto.toDomain(): Chat? {
    return Chat(
        id = getUuidOrNull(id) ?: return null,
        imageUrl = imageUrl,
        name = name,
        requesterId = getUuidOrNull(requesterId) ?: return null
    )
}

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
fun Message.toPendingMessageLocalDto(): PendingMessageLocalDto {
    val content = this.content
    val text = if (content is MessageContent.Text) content.text else null
    val data = if (content is MessageContent.Image) content.data else null
    val image = if (data is ImageData.ImageByteArray) data.byteArray else null


    return PendingMessageLocalDto(
        id = this.id.toString(),
        senderId = this.senderId.toString(),
        text = text,
        image = image,
        timestamp = this.sendAt.toInstant().toEpochMilliseconds(),
        chatId = this.chatId.toString(),
        status = status
    )
}


@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
fun Message.toCachedMessageLocalDto(): CachedMessageLocalDto {
    val content = this.content
    val text = if (content is MessageContent.Text) content.text else null
    val data = if (content is MessageContent.Image) content.data else null
    val image = if (data is ImageData.ImageUrl) data.url else null


    return CachedMessageLocalDto(
        id = this.id.toString(),
        senderId = this.senderId.toString(),
        text = text,
        imageUrl = image,
        timestamp = this.sendAt.toInstant().toEpochMilliseconds(),
        chatId = this.chatId.toString(),
        isMine = this.isMine,
        status = status
    )
}

fun List<Message>.toCachedMessageLocalDto(): List<CachedMessageLocalDto> = map { it.toCachedMessageLocalDto() }

fun CachedMessageLocalDto.toDomain(): Message {
    val content = if (text != null) {
        MessageContent.Text(text)
    } else if (imageUrl != null) {
        MessageContent.Image(ImageData.ImageUrl(imageUrl))
    } else {
        error("Invalid message content")
    }

    return Message(
        id = Uuid.parse(this.id),
        senderId = Uuid.parse(this.senderId),
        chatId = Uuid.parse(this.chatId),
        content = content,
        sendAt = Instant.fromEpochMilliseconds(this.timestamp).toLocalDateTime(),
        status = status,
        isMine = isMine
    )
}

fun PendingMessageLocalDto.toDomain(): Message {
    val content = if (text != null) {
        MessageContent.Text(text)
    } else if (image != null) {
        MessageContent.Image(ImageData.ImageByteArray(image))
    } else {
        error("Invalid message content")
    }

    return Message(
        id = Uuid.parse(this.id),
        senderId = Uuid.parse(this.senderId),
        chatId = Uuid.parse(this.chatId),
        content = content,
        sendAt = Instant.fromEpochMilliseconds(this.timestamp).toLocalDateTime(),
        status = status,
        isMine = true
    )
}

fun List<PendingMessageLocalDto>.toDomain(): List<Message> = map { it.toDomain() }

fun MarkAsReadDto.toDomain(): MarkMessageAsReadEvent {
    return MarkMessageAsReadEvent(
        readByUserId = Uuid.parse(readByUserId),
        chatId = Uuid.parse(chatId),
        readByMe = readByMe
    )
}

fun DeleteChatDto.toDomain(): DeleteChatEvent {
    return DeleteChatEvent(
        chatId = Uuid.parse(chatId),
    )
}

fun List<MessageDto>.toListOfMessages(): List<Message> {
    return mapNotNull { it.toDomain() }
}


fun PagedDataDto<MessageDto>.toPagedListOfMessages(): PagedData<Message> {
    return PagedData(
        data = data
            .orEmpty()
            .toListOfMessages(),
        totalItems = totalItems ?: 0,
        isLastPage = (pageNumber ?: 0) >= (totalPages ?: 0)
    )
}