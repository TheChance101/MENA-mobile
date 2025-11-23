@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package net.thechance.mena.core_chat.data.source.remote.mapper

import net.thechance.mena.core_chat.data.source.local.database.cachedChat.CachedChatLocalDto
import net.thechance.mena.core_chat.data.source.local.database.cachedMessage.CachedMessageLocalDto
import net.thechance.mena.core_chat.data.source.local.database.cachedMessage.MessageContentLocalDto
import net.thechance.mena.core_chat.data.source.local.database.cachedMessage.MessageReactionLocalDto
import net.thechance.mena.core_chat.data.source.local.database.pendingMessage.PendingMessageContentLocalDto
import net.thechance.mena.core_chat.data.source.local.database.pendingMessage.PendingMessageLocalDto
import net.thechance.mena.core_chat.data.source.remote.dto.ChatDto
import net.thechance.mena.core_chat.data.source.remote.dto.MarkAsReadDto
import net.thechance.mena.core_chat.data.source.remote.dto.MessageContentDto
import net.thechance.mena.core_chat.data.source.remote.dto.MessageDto
import net.thechance.mena.core_chat.data.source.remote.dto.MessageReactionDto
import net.thechance.mena.core_chat.data.source.remote.dto.PagedDataDto
import net.thechance.mena.core_chat.data.source.remote.dto.events.DeleteChatDto
import net.thechance.mena.core_chat.data.utils.toInstant
import net.thechance.mena.core_chat.data.utils.toLocalDateTime
import net.thechance.mena.core_chat.data.utils.toUuid
import net.thechance.mena.core_chat.domain.entity.AudioData.AudioByteArray
import net.thechance.mena.core_chat.domain.entity.AudioData.AudioUrl
import net.thechance.mena.core_chat.domain.entity.Chat
import net.thechance.mena.core_chat.domain.entity.ImageData.ImageByteArray
import net.thechance.mena.core_chat.domain.entity.ImageData.ImageUrl
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


fun MessageDto.toDomain(): Message {
    return Message(
        id = (id).toUuid(),
        senderId = (senderId).toUuid(),
        chatId = (chatId).toUuid(),
        sendAt = Instant.parse(sendAt).toLocalDateTime(),
        status = if (isRead) MessageStatus.READ else MessageStatus.SENT,
        content = content.toDomain(),
        reactions = reactions.map(MessageReactionDto::toDomain),
        isMine = isMine
    )
}

fun MessageContentDto.toDomain(): MessageContent {
    return when (this) {
        is MessageContentDto.Text -> MessageContent.Text(text)
        is MessageContentDto.Image -> MessageContent.Image(ImageUrl(url))
        is MessageContentDto.Audio -> MessageContent.Audio(AudioUrl(url), duration)
        is MessageContentDto.Money -> MessageContent.Money(amount = amount)
        is MessageContentDto.Ayah -> {
            MessageContent.Ayah(
                surahId = surahNumber,
                ayahContent = ayahContent,
                ayahNumber = ayahNumber,
                surahName = ""
            )
        }

        is MessageContentDto.Order -> {
            MessageContent.Order(
                orderId = orderId.toUuid(),
                numberOfItems = totalProducts,
                deliverTo = deliverToAddress,
                totalPrice = totalPrice.toDouble()
            )
        }
    }
}

fun MessageReactionDto.toDomain(): MessageReaction {
    return MessageReaction(
        emoji = emoji,
        userId = userId.toUuid(),
        messageId = messageId.toUuid(),
    )
}

fun ChatDto.toDomain(): Chat {
    return Chat(
        id = id.toUuid(),
        imageUrl = imageUrl,
        name = name,
        requesterId = requesterId.toUuid(),
        receiverId = receiverId.toUuid()
    )
}

fun ChatDto.toLocalDto(): CachedChatLocalDto {
    return CachedChatLocalDto(
        id = id,
        imageUrl = imageUrl,
        name = name,
        requesterId = requesterId,
        receiverId = receiverId
    )
}

fun CachedChatLocalDto.toDomain(): Chat {
    return Chat(
        id = id.toUuid(),
        imageUrl = imageUrl,
        name = name,
        requesterId = requesterId.toUuid(),
        receiverId = receiverId.toUuid()
    )
}

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
fun Message.toPendingMessageLocalDto(): PendingMessageLocalDto {
    return PendingMessageLocalDto(
        id = this.id.toString(),
        senderId = this.senderId.toString(),
        content = content.toPendingLocalDto(),
        timestamp = this.sendAt.toInstant().toEpochMilliseconds(),
        chatId = this.chatId.toString(),
        status = status
    )
}

fun MessageContent.toPendingLocalDto(): PendingMessageContentLocalDto {
    return when (this) {
        is MessageContent.Audio -> {
            when (val audioData = this.data) {
                is AudioUrl -> error("Invalid message content")
                is AudioByteArray -> PendingMessageContentLocalDto.Audio(
                    audioData.byteArray,
                    audioDurationMs ?: 0L
                )
            }
        }

        is MessageContent.Image -> {
            when (val imageData = this.data) {
                is ImageUrl -> error("Invalid message content")
                is ImageByteArray -> PendingMessageContentLocalDto.Image(imageData.byteArray)
            }
        }

        is MessageContent.Ayah -> PendingMessageContentLocalDto.Ayah(
            surahId,
            ayahNumber,
            ayahContent
        )

        is MessageContent.Text -> PendingMessageContentLocalDto.Text(text)
        is MessageContent.Money -> PendingMessageContentLocalDto.Money(amount)
        is MessageContent.Order -> PendingMessageContentLocalDto.Order(
            orderId = orderId.toString(),
            totalProducts = numberOfItems,
            totalPrice = totalPrice,
            deliverToAddress = deliverTo
        )
    }
}

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
fun Message.toCachedMessageLocalDto(): CachedMessageLocalDto {
    return CachedMessageLocalDto(
        id = this.id.toString(),
        content = content.toLocalDto(),
        senderId = this.senderId.toString(),
        reactions = reactions.toLocalDto(),
        timestamp = this.sendAt.toInstant().toEpochMilliseconds(),
        chatId = this.chatId.toString(),
        isMine = this.isMine,
        status = status
    )
}

fun MessageContent.toLocalDto(): MessageContentLocalDto {
    return when (this) {
        is MessageContent.Audio -> {
            when (val audioData = this.data) {
                is AudioUrl -> MessageContentLocalDto.Audio(
                    audioData.url,
                    audioDurationMs ?: 0L
                )

                is AudioByteArray -> error("Invalid message content")
            }
        }

        is MessageContent.Image -> {
            when (val imageData = this.data) {
                is ImageUrl -> MessageContentLocalDto.Image(imageData.url)
                is ImageByteArray -> error("Invalid message content")
            }
        }

        is MessageContent.Ayah -> MessageContentLocalDto.Ayah(surahId, ayahNumber, ayahContent)
        is MessageContent.Text -> MessageContentLocalDto.Text(text)
        is MessageContent.Money -> MessageContentLocalDto.Money(amount)
        is MessageContent.Order -> MessageContentLocalDto.Order(
            orderId = orderId.toString(),
            totalProducts = numberOfItems,
            totalPrice = totalPrice,
            deliverToAddress = deliverTo
        )
    }
}

fun MessageReaction.toLocalDto(): MessageReactionLocalDto {
    return MessageReactionLocalDto(
        emoji = emoji,
        userId = userId,
        messageId = messageId
    )
}

fun List<MessageReaction>.toLocalDto(): List<MessageReactionLocalDto> =
    map(MessageReaction::toLocalDto)

fun MessageReactionLocalDto.toDomain(): MessageReaction {
    return MessageReaction(
        emoji = emoji,
        userId = userId,
        messageId = messageId
    )
}

fun List<MessageReactionLocalDto>.toDomainMessageReaction(): List<MessageReaction> =
    map(MessageReactionLocalDto::toDomain)

fun List<Message>.toCachedMessageLocalDto(): List<CachedMessageLocalDto> =
    map { it.toCachedMessageLocalDto() }

fun CachedMessageLocalDto.toDomain(): Message {
    return Message(
        id = Uuid.parse(this.id),
        senderId = Uuid.parse(this.senderId),
        chatId = Uuid.parse(this.chatId),
        content = content.toDomain(),
        reactions = reactions.toDomainMessageReaction(),
        sendAt = Instant.fromEpochMilliseconds(this.timestamp).toLocalDateTime(),
        status = status,
        isMine = isMine
    )
}

fun MessageContentLocalDto.toDomain(): MessageContent {
    return when (this) {
        is MessageContentLocalDto.Audio -> MessageContent.Audio(AudioUrl(url), durationMs)
        is MessageContentLocalDto.Ayah -> MessageContent.Ayah(surahId, "", ayahText, ayahNumber)
        is MessageContentLocalDto.Image -> MessageContent.Image(ImageUrl(url))
        is MessageContentLocalDto.Text -> MessageContent.Text(text)
        is MessageContentLocalDto.Money -> MessageContent.Money(amount)
        is MessageContentLocalDto.Order -> MessageContent.Order(
            orderId = orderId.toUuid(),
            numberOfItems = totalProducts,
            deliverTo = deliverToAddress,
            totalPrice = totalPrice
        )
    }
}

fun PendingMessageLocalDto.toDomain(): Message {
    return Message(
        id = Uuid.parse(this.id),
        senderId = Uuid.parse(this.senderId),
        chatId = Uuid.parse(this.chatId),
        content = content.toDomain(),
        sendAt = Instant.fromEpochMilliseconds(this.timestamp).toLocalDateTime(),
        status = status,
        isMine = true
    )
}

fun PendingMessageContentLocalDto.toDomain(): MessageContent {
    return when (this) {
        is PendingMessageContentLocalDto.Audio -> MessageContent.Audio(
            AudioByteArray(bytes),
            durationMs
        )

        is PendingMessageContentLocalDto.Image -> MessageContent.Image(ImageByteArray(bytes))
        is PendingMessageContentLocalDto.Ayah -> MessageContent.Ayah(
            surahId,
            "",
            ayahText,
            ayahNumber
        )

        is PendingMessageContentLocalDto.Text -> MessageContent.Text(text)
        is PendingMessageContentLocalDto.Money -> MessageContent.Money(amount)
        is PendingMessageContentLocalDto.Order -> MessageContent.Order(
            orderId = orderId.toUuid(),
            numberOfItems = totalProducts,
            deliverTo = deliverToAddress,
            totalPrice = totalPrice
        )
    }
}

fun List<PendingMessageLocalDto>.toDomain(): List<Message> =
    map(PendingMessageLocalDto::toDomain)

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
        data = data.toListOfMessages(),
        totalItems = totalItems,
        isLastPage = pageNumber >= totalPages
    )
}