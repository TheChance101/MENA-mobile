package net.thechance.mena.core_chat.data.source.local.database

import androidx.room.TypeConverter
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
    fun fromByteArray(value: ByteArray?): String? {
        return value?.let { Base64.encode(it) }
    }

    @TypeConverter
    fun toByteArray(value: String?): ByteArray? {
        return value?.let { Base64.decode(it) }
    }

}