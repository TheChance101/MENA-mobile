package net.thechance.mena.core_chat.data.source.local.database.pendingMessage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.thechance.mena.core_chat.domain.entity.MessageStatus

@Entity(tableName = "pending_messages")
data class PendingMessageLocalDto(
    @PrimaryKey val id: String,
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
    @ColumnInfo(name = "surah_id")
    val surahId: Int? = null,
    @ColumnInfo(name = "surah_name")
    val surahName: String? = null,
    @ColumnInfo(name = "ayah_number")
    val ayahNumber: Int? = null,
    @ColumnInfo(name = "ayah_text")
    val ayahText: String? = null,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long,
    @ColumnInfo(name = "chat_id")
    val chatId: String,
    @ColumnInfo(name = "status")
    val status: MessageStatus,
)