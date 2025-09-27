package net.thechance.mena.core_chat.data.database

import androidx.room.TypeConverter
import net.thechance.mena.core_chat.data.database.entity.MessageStatus

class MessageStatusConverter {

    @TypeConverter
    fun fromMessageStatus(status: MessageStatus): String = status.name

    @TypeConverter
    fun toMessageStatus(status: String): MessageStatus = runCatching { MessageStatus.valueOf(status) }.getOrDefault(MessageStatus.FAILED)

}