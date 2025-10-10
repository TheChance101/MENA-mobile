package net.thechance.mena.core_chat.data.source.local.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageLocalDto(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "sender_id")
    val senderId: String,
    @ColumnInfo(name = "message_text")
    val text: String,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long,
    @ColumnInfo(name = "chat_id")
    val chatId: String,
    @ColumnInfo(name = "message_status")
    val status: MessageStatus,
) {
    enum class MessageStatus {
        LOADING,
        SENT,
        READ,
        FAILED
    }
}