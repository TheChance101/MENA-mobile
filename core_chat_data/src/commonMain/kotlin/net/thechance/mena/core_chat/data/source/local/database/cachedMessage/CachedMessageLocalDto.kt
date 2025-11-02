package net.thechance.mena.core_chat.data.source.local.database.cachedMessage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.thechance.mena.core_chat.domain.entity.MessageStatus

@Entity(tableName = "cached_messages")
data class CachedMessageLocalDto(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "sender_id")
    val senderId: String,
    @ColumnInfo(name = "text")
    val text: String? = null,
    @ColumnInfo(name = "image")
    val imageUrl: String? = null,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long,
    @ColumnInfo(name = "chat_id")
    val chatId: String,
    @ColumnInfo(name = "status")
    val status: MessageStatus,
)