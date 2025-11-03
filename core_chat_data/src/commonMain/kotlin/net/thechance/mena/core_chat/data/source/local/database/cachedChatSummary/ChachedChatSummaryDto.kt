package net.thechance.mena.core_chat.data.source.local.database.cachedChatSummary

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun ChatSummary.toCached(): CachedChatSummaryDto {
    return CachedChatSummaryDto(
        id = this.id.toString(),
        name = this.name,
        imageUrl = this.imageUrl,
        lastMessageContent = this.lastMessage?.content,
        lastMessageSentAt = this.lastMessage?.sendAt?.toString(),
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
        lastMessage = this.lastMessageContent?. let {
            ChatSummary.Message(
                content = this.lastMessageContent,
                sendAt =  Instant.parse(this.lastMessageSentAt.orEmpty()).toLocalDateTime(TimeZone.currentSystemDefault()),
                isMine = this.lastMessageIsMine ?: false
            )
        },
        unReadMessagesCount = unReadMessagesCount
    )
}