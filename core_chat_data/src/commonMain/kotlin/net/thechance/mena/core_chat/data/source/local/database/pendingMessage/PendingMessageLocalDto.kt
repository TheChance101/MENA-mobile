package net.thechance.mena.core_chat.data.source.local.database.pendingMessage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import net.thechance.mena.core_chat.data.source.local.database.cachedChat.CachedChatLocalDto
import net.thechance.mena.core_chat.domain.entity.MessageStatus

@Entity(
    tableName = "pending_messages",
    primaryKeys = ["id", "chat_id"],
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
data class PendingMessageLocalDto(
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "sender_id")
    val senderId: String,
    @ColumnInfo(name = "text")
    val text: String? = null,
    @ColumnInfo(name = "image")
    val image: ByteArray? = null,
    @ColumnInfo(name = "video")
    val audio: ByteArray? = null,
    @ColumnInfo(name = "audio_duration_ms")
    val audioDurationMs: Long? = null,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long,
    @ColumnInfo(name = "chat_id")
    val chatId: String,
    @ColumnInfo(name = "status")
    val status: MessageStatus,
)