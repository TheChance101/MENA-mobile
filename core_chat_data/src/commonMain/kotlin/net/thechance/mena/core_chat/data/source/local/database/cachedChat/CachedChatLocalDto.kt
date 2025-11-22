package net.thechance.mena.core_chat.data.source.local.database.cachedChat

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_chats")
data class CachedChatLocalDto(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null,
    @ColumnInfo(name = "requester_id")
    val requesterId: String,
    @ColumnInfo(name = "receiver_id")
    val receiverId: String ,
)