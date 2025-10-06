package net.thechance.mena.core_chat.data.local_database

import androidx.room.TypeConverter
import net.thechance.mena.core_chat.data.local_database.dto.MessageLocalDto

class MessageStatusConverter {
    @TypeConverter
    fun fromMessageStatus(status: MessageLocalDto.MessageStatus): String = status.name

    @TypeConverter
    fun toMessageStatus(status: String): MessageLocalDto.MessageStatus {
        return runCatching { MessageLocalDto.MessageStatus.valueOf(status) }
            .getOrDefault(MessageLocalDto.MessageStatus.FAILED)
    }
}