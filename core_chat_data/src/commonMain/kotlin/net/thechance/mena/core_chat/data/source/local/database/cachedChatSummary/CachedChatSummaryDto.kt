package net.thechance.mena.core_chat.data.source.local.database.cachedChatSummary

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_chat_summary")
data class CachedChatSummaryDto(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "image_url")
    val imageUrl: String,
    @ColumnInfo(name = "last_message_type")
    val lastMessageType: String?,
    @ColumnInfo(name = "last_message_text")
    val lastMessageText: String?,
    @ColumnInfo(name = "last_message_send_at")
    val lastMessageSentAt: String?,
    @ColumnInfo(name = "last_message_is_mine")
    val lastMessageIsMine: Boolean?,
    @ColumnInfo(name = "unread_message_count")
    val unReadMessagesCount: Int
)