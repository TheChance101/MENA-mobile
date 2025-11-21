@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.data.source.local.database.cachedMessage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity(tableName = "cached_messages")
data class CachedMessageLocalDto(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "sender_id")
    val senderId: String,
    @ColumnInfo(name = "text")
    val text: String? = null,
    @ColumnInfo(name = "image")
    val imageUrl: String? = null,
    @ColumnInfo(name = "audio")
    val audioUrl: String? = null,
    @ColumnInfo(name = "audio_duration")
    val audioDurationMs: Long? = null,
    @ColumnInfo(name = "surah_id")
    val surahId: Int? = null,
    @ColumnInfo(name = "surah_name")
    val surahName: String? = null,
    @ColumnInfo(name = "ayah_number")
    val ayahNumber: Int? = null,
    @ColumnInfo(name = "ayah_text")
    val ayahText: String? = null,
    @ColumnInfo(name = "reactions")
    val reactions: List<MessageReactionLocalDto>,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long,
    @ColumnInfo(name = "chat_id")
    val chatId: String,
    @ColumnInfo(name = "status")
    val status: MessageStatus,
    @ColumnInfo(name = "is_mine")
    val isMine: Boolean,
    @ColumnInfo(name = "order_id")
    val orderId: String?,
    @ColumnInfo(name = "number_of_items")
    val numberOfItems: Int?,
    @ColumnInfo(name = "deliver_to")
    val deliverTo: String?,
    @ColumnInfo(name = "total_price")
    val totalPrice: Double?
)


@Serializable
data class MessageReactionLocalDto(
    val emoji: String,
    val userId: Uuid,
    val messageId: Uuid
)