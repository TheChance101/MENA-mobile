package net.thechance.mena.core_chat.data.source.local.database.chatSyncTime

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import net.thechance.mena.core_chat.data.source.local.database.cachedChat.CachedChatLocalDto

@Entity(
    tableName = "chat_sync_times",
    primaryKeys = ["chat_id"],
    foreignKeys = [
        ForeignKey(
            entity = CachedChatLocalDto::class,
            parentColumns = ["id"],
            childColumns = ["chat_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["chat_id"])
    ]
)
data class ChatSyncTimeLocalDto(
    @ColumnInfo(name = "chat_id")
    val chatId: String,
    @ColumnInfo(name = "last_sync_time")
    val lastSyncTime: String
)
