package net.thechance.mena.core_chat.data.source.local.database.cachedChatSummary

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun ChatSummary.toCached(): CachedChatSummaryDto {
    return CachedChatSummaryDto(
        id = this.id.toString(),
        name = this.name,
        imageUrl = this.imageUrl,
        lastMessageId = this.lastMessage?.id?.toString(),
        lastMessageSenderId = this.lastMessage?.senderId?.toString(),
        lastMessageContent = (this.lastMessage?.content as? MessageContent.Text)?.text,
        lastMessageSentAt = this.lastMessage?.sendAt.toString(),
        lastMessageIsMine = this.lastMessage?.isMine,
        unReadMessagesCount = this.unReadMessagesCount
    )
}

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
fun CachedChatSummaryDto.toDomain(): ChatSummary {
    return ChatSummary(
        id = Uuid.parse(id),
        name = name,
        imageUrl = imageUrl,
        lastMessage = this.lastMessageContent?.let {
            Message(
                id = lastMessageId?.let { Uuid.parse(it) } ?: Uuid.random(),
                senderId = lastMessageSenderId?.let { Uuid.parse(it) } ?: Uuid.random(),
                chatId = Uuid.parse(id),
                sendAt = lastMessageSentAt?.let { LocalDateTime.parse(it) }
                    ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                status = MessageStatus.SENT,
                content = MessageContent.Text(this.lastMessageContent),
                isMine = this.lastMessageIsMine ?: false
            )
        },
        unReadMessagesCount = unReadMessagesCount
    )
}