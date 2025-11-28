package net.thechance.mena.core_chat.data.source.local.database.cachedChatSummary

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.domain.entity.LastMessageType
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun ChatSummary.toCached(): CachedChatSummaryDto {
    val messageType = this.lastMessage?.type
    val cachedType = messageType?.let { type ->
        when (type) {
            is LastMessageType.Text -> CachedMessageType.TEXT
            is LastMessageType.Image -> CachedMessageType.IMAGE
            is LastMessageType.Audio -> CachedMessageType.AUDIO
            is LastMessageType.Money -> CachedMessageType.MONEY
            is LastMessageType.Ayah -> CachedMessageType.AYAH
            is LastMessageType.Order -> CachedMessageType.ORDER
        }
    }
    val messageText = (messageType as? LastMessageType.Text)?.text

    return CachedChatSummaryDto(
        id = this.id.toString(),
        name = this.name,
        imageUrl = this.imageUrl,
        lastMessageType = cachedType?.name,
        lastMessageText = messageText,
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
        lastMessage = this.lastMessageType?.let { typeString ->
            val cachedType = CachedMessageType.fromString(typeString)
            val messageType = when (cachedType) {
                CachedMessageType.TEXT -> LastMessageType.Text(lastMessageText ?: "")
                CachedMessageType.IMAGE -> LastMessageType.Image
                CachedMessageType.AUDIO -> LastMessageType.Audio
                CachedMessageType.MONEY -> LastMessageType.Money
                CachedMessageType.AYAH -> LastMessageType.Ayah
                CachedMessageType.ORDER -> LastMessageType.Order
                null -> LastMessageType.Text("") // Fallback for unknown types
            }
            ChatSummary.Message(
                type = messageType,
                sendAt = lastMessageSentAt?.let { LocalDateTime.parse(it) }
                    ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                isMine = this.lastMessageIsMine ?: false
            )
        },
        unReadMessagesCount = unReadMessagesCount
    )
}