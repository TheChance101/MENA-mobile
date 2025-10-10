package net.thechance.mena.core_chat.data.source.local.database

import androidx.room.TypeConverter

class MessageStatusConverter {
    @TypeConverter
    fun fromMessageStatus(status: MessageLocalDto.MessageStatus): String = status.name

    @TypeConverter
    fun toMessageStatus(status: String): MessageLocalDto.MessageStatus {
        return runCatching { MessageLocalDto.MessageStatus.valueOf(status) }
            .getOrDefault(MessageLocalDto.MessageStatus.FAILED)
    }
}