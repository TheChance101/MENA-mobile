package net.thechance.mena.core_chat.data.source.local.database

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import net.thechance.mena.core_chat.data.source.local.database.cachedMessage.MessageReactionLocalDto
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import kotlin.io.encoding.Base64

class MessageConverter {
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

}