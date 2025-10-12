package net.thechance.mena.core_chat.data.source.local.database

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64

class MessageConverter {
    @TypeConverter
    fun fromMessageStatus(status: MessageLocalDto.MessageStatus): String = status.name

    @TypeConverter
    fun toMessageStatus(status: String): MessageLocalDto.MessageStatus {
        return runCatching { MessageLocalDto.MessageStatus.valueOf(status) }
            .getOrDefault(MessageLocalDto.MessageStatus.FAILED)
    }

    @TypeConverter
    fun fromByteArrayList(value: List<ByteArray>?): String? {
        return value?.map { Base64.encode(it) }
            ?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toByteArrayList(value: String?): List<ByteArray>? {
        return value?.let { Json.decodeFromString<List<String>>(it) }
            ?.map { Base64.decode(it) }
    }
}