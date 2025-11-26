package net.thechance.mena.core_chat.data.source.local.database

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import net.thechance.mena.core_chat.data.source.local.database.cachedMessage.MessageContentLocalDto
import net.thechance.mena.core_chat.data.source.local.database.cachedMessage.MessageReactionLocalDto
import net.thechance.mena.core_chat.data.source.local.database.pendingMessage.PendingMessageContentLocalDto
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import kotlin.io.encoding.Base64

class MessageConverter {
    private val messageJson = Json { classDiscriminator = "type" }

    @TypeConverter
    fun fromMessageStatus(status: MessageStatus): String = status.name

    @TypeConverter
    fun toMessageStatus(status: String): MessageStatus {
        return runCatching { MessageStatus.valueOf(status) }
            .getOrDefault(MessageStatus.FAILED)
    }

    @TypeConverter
    fun fromByteArray(value: ByteArray?): String? {
        return value?.let { Base64.encode(it) }
    }

    @TypeConverter
    fun toByteArray(value: String?): ByteArray? {
        return value?.let { Base64.decode(it) }
    }

    @TypeConverter
    fun fromReactions(value: List<MessageReactionLocalDto>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toReactions(value: String): List<MessageReactionLocalDto> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromMessageContent(content: MessageContentLocalDto): String {
        return messageJson.encodeToString<MessageContentLocalDto>(content)
    }

    @TypeConverter
    fun toMessageContent(json: String): MessageContentLocalDto {
        return messageJson.decodeFromString<MessageContentLocalDto>(json)
    }

    @TypeConverter
    fun fromPendingMessageContent(content: PendingMessageContentLocalDto): String {
        return messageJson.encodeToString<PendingMessageContentLocalDto>(content)
    }

    @TypeConverter
    fun toPendingMessageContent(json: String): PendingMessageContentLocalDto {
        return messageJson.decodeFromString<PendingMessageContentLocalDto>(json)
    }

}