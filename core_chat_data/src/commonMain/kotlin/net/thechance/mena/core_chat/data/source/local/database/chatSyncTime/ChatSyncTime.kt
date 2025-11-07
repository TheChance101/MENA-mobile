package net.thechance.mena.core_chat.data.source.local.database.chatSyncTime

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_sync_times")
data class ChatSyncTime(
    @PrimaryKey val chatId: String,
    val lastSyncTime: String
)
