package net.thechance.mena.core_chat.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MessageEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "sender_id") val senderId: String,
    @ColumnInfo(name = "message_text") val text: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "chat_id") val chatId: String,
    @ColumnInfo(name = "message_status") val status: MessageStatus,
)

enum class MessageStatus {
    SENDING,
    SENT,
    READ,
    FAILED
}