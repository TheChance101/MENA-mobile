@file:OptIn(ExperimentalUuidApi::class, ExperimentalSerializationApi::class)

package net.thechance.mena.core_chat.data.source.local.database.cachedMessage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.thechance.mena.core_chat.data.source.local.database.cachedChat.CachedChatLocalDto
import kotlinx.serialization.json.JsonClassDiscriminator
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity(
    tableName = "cached_messages",
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

data class CachedMessageLocalDto(
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "sender_id")
    val senderId: String,
    @ColumnInfo(name = "content")
    val content: MessageContentLocalDto,
    @ColumnInfo(name = "reactions")
    val reactions: List<MessageReactionLocalDto>,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long,
    @ColumnInfo(name = "chat_id")
    val chatId: String,
    @ColumnInfo(name = "status")
    val status: MessageStatus,
    @ColumnInfo(name = "is_mine")
    val isMine: Boolean
)

@Serializable
data class MessageReactionLocalDto(
    val emoji: String,
    val userId: Uuid,
    val messageId: Uuid
)

@Serializable
@JsonClassDiscriminator("type")
sealed class MessageContentLocalDto {

    @Serializable
    @SerialName("text")
    data class Text(val text: String) : MessageContentLocalDto()

    @Serializable
    @SerialName("image")
    data class Image(val url: String) : MessageContentLocalDto()

    @Serializable
    @SerialName("audio")
    data class Audio(
        val url: String,
        val durationMs: Long
    ) : MessageContentLocalDto()

    @Serializable
    @SerialName("ayah")
    data class Ayah(
        val surahId: Int,
        val ayahNumber: Int,
        val ayahText: String
    ) : MessageContentLocalDto()

    @Serializable
    @SerialName("money")
    data class Money(val amount: Double) : MessageContentLocalDto()


    @Serializable
    @SerialName("order")
    data class Order(
        val orderId: String,
        val totalProducts: Int,
        val totalPrice: Double,
        val deliverToAddress: String
    ) : MessageContentLocalDto()

}
