@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package net.thechance.mena.core_chat.data.source.remote.mapper

import net.thechance.mena.core_chat.data.source.local.database.cachedChat.CachedChatLocalDto
import net.thechance.mena.core_chat.data.source.local.database.cachedMessage.CachedMessageLocalDto
import net.thechance.mena.core_chat.data.source.local.database.cachedMessage.MessageReactionLocalDto
import net.thechance.mena.core_chat.data.source.local.database.pendingMessage.PendingMessageLocalDto
import net.thechance.mena.core_chat.data.source.remote.dto.*
import net.thechance.mena.core_chat.data.source.remote.dto.events.DeleteChatDto
import net.thechance.mena.core_chat.data.source.remote.dto.MessageContentDto
import net.thechance.mena.core_chat.data.utils.toInstant
import net.thechance.mena.core_chat.data.utils.toLocalDateTime
import net.thechance.mena.core_chat.data.utils.toUuid
import net.thechance.mena.core_chat.domain.entity.*
import net.thechance.mena.core_chat.domain.entity.AudioData.*
import net.thechance.mena.core_chat.domain.entity.ImageData.*
import net.thechance.mena.core_chat.domain.event.DeleteChatEvent
import net.thechance.mena.core_chat.domain.event.MarkMessageAsReadEvent
import net.thechance.mena.core_chat.domain.model.PagedData
import net.thechance.mena.faith.domain.service.QuranService
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


suspend fun MessageDto.toDomain(quranService: QuranService): Message {
    return Message(
        id = (id).toUuid(),
        senderId = (senderId).toUuid(),
        chatId = (chatId).toUuid(),
        sendAt = Instant.parse(sendAt).toLocalDateTime(),
        status = if (isRead) MessageStatus.READ else MessageStatus.SENT,
        content = content.toDomain(quranService),
        reactions = reactions.map(MessageReactionDto::toDomain),
        isMine = isMine
    )
}

suspend fun MessageContentDto.toDomain(quranService: QuranService): MessageContent {
    return when (this) {
        is MessageContentDto.Text -> MessageContent.Text(text)
        is MessageContentDto.Image -> MessageContent.Image(ImageUrl(url))
        is MessageContentDto.Audio -> MessageContent.Audio(AudioUrl(url), duration)
        is MessageContentDto.Money -> MessageContent.Text(amount.toString())
        is MessageContentDto.Ayah -> {
            val surahName = quranService.getSurahDetails(surahNumber).name
            MessageContent.Ayah(
                surahId = surahNumber,
                ayahContent = ayahContent,
                ayahNumber = ayahNumber,
                surahName = surahName
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
        requesterId = requesterId.toUuid()
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

fun CachedChatLocalDto.toDomain(): Chat {
    return Chat(
        id = id.toUuid(),
        imageUrl = imageUrl,
        name = name,
        requesterId = requesterId.toUuid(),
    )
}

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
fun Message.toPendingMessageLocalDto(): PendingMessageLocalDto {
    val content = this.content
    val text = if (content is MessageContent.Text) content.text else null
    val imageData = if (content is MessageContent.Image) content.data else null
    val image = if (imageData is ImageData.ImageByteArray) imageData.byteArray else null
    val audioData = if (content is MessageContent.Audio) content.data else null
    val audioDuration = if (content is MessageContent.Audio) content.audioDurationMs else null
    val audio = if (audioData is AudioData.AudioByteArray) audioData.byteArray else null
    val surahId = if (content is MessageContent.Ayah) content.surahId else null
    val surahName = if (content is MessageContent.Ayah) content.surahName else null
    val ayahText = if (content is MessageContent.Ayah) content.ayahContent else null
    val ayahNumber = if (content is MessageContent.Ayah) content.ayahNumber else null


    return PendingMessageLocalDto(
        id = this.id.toString(),
        senderId = this.senderId.toString(),
        text = text,
        image = image,
        audio = audio,
        audioDurationMs = audioDuration,
        surahId = surahId,
        surahName = surahName,
        ayahText = ayahText,
        ayahNumber = ayahNumber,
        timestamp = this.sendAt.toInstant().toEpochMilliseconds(),
        chatId = this.chatId.toString(),
        status = status
    )
}


@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
fun Message.toCachedMessageLocalDto(): CachedMessageLocalDto {
    val content = this.content
    val text = if (content is MessageContent.Text) content.text else null
    val imageData = if (content is MessageContent.Image) content.data else null
    val imageUrl = if (imageData is ImageData.ImageUrl) imageData.url else null
    val audioData = if (content is MessageContent.Audio) content.data else null
    val audioDuration = if (content is MessageContent.Audio) content.audioDurationMs else null
    val audioUrl = if (audioData is AudioData.AudioUrl) audioData.url else null
    val surahId = if (content is MessageContent.Ayah) content.surahId else null
    val surahName = if (content is MessageContent.Ayah) content.surahName else null
    val ayahText = if (content is MessageContent.Ayah) content.ayahContent else null
    val ayahNumber = if (content is MessageContent.Ayah) content.ayahNumber else null

    return CachedMessageLocalDto(
        id = this.id.toString(),
        senderId = this.senderId.toString(),
        text = text,
        imageUrl = imageUrl,
        audioUrl = audioUrl,
        audioDurationMs = audioDuration,
        surahId = surahId,
        surahName = surahName,
        ayahText = ayahText,
        ayahNumber = ayahNumber,
        reactions = reactions.toLocalDto(),
        timestamp = this.sendAt.toInstant().toEpochMilliseconds(),
        chatId = this.chatId.toString(),
        isMine = this.isMine,
        status = status
    )
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
    val content = when {
        text != null -> MessageContent.Text(text)
        imageUrl != null -> MessageContent.Image(ImageData.ImageUrl(imageUrl))
        audioUrl != null -> MessageContent.Audio(AudioData.AudioUrl(audioUrl), audioDurationMs)
        ayahText != null && surahName != null && surahId != null && ayahNumber != null ->
            MessageContent.Ayah(surahId, surahName, ayahText, ayahNumber)

        else -> error("Invalid message content")
    }

    return Message(
        id = Uuid.parse(this.id),
        senderId = Uuid.parse(this.senderId),
        chatId = Uuid.parse(this.chatId),
        content = content,
        reactions = reactions.toDomainMessageReaction(),
        sendAt = Instant.fromEpochMilliseconds(this.timestamp).toLocalDateTime(),
        status = status,
        isMine = isMine
    )
}

fun PendingMessageLocalDto.toDomain(): Message {
    val content = when {
        text != null -> MessageContent.Text(text)
        image != null -> MessageContent.Image(ImageData.ImageByteArray(image))
        audio != null -> MessageContent.Audio(AudioData.AudioByteArray(audio), audioDurationMs)
        ayahText != null && surahName != null && surahId != null && ayahNumber != null ->
            MessageContent.Ayah(surahId, surahName, ayahText, ayahNumber)
        else -> error("Invalid message content")
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

fun List<PendingMessageLocalDto>.toDomain(): List<Message> = map(PendingMessageLocalDto::toDomain)

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

suspend fun List<MessageDto>.toListOfMessages(quranService: QuranService): List<Message> {
    return mapNotNull { it.toDomain(quranService) }
}

suspend fun PagedDataDto<MessageDto>.toPagedListOfMessages(quranService: QuranService): PagedData<Message> {
    return PagedData(
        data = data.toListOfMessages(quranService),
        totalItems = totalItems,
        isLastPage = pageNumber >= totalPages
    )
}