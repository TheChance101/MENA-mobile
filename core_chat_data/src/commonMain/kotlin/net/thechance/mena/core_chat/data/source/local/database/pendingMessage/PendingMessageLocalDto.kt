@file:OptIn(ExperimentalSerializationApi::class)

package net.thechance.mena.core_chat.data.source.local.database.pendingMessage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import net.thechance.mena.core_chat.domain.entity.MessageStatus

@Entity(tableName = "pending_messages")
data class PendingMessageLocalDto(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "sender_id")
    val senderId: String,
    @ColumnInfo(name = "content")
    val content: PendingMessageContentLocalDto,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long,
    @ColumnInfo(name = "chat_id")
    val chatId: String,
    @ColumnInfo(name = "status")
    val status: MessageStatus,
)

@Serializable
@JsonClassDiscriminator("type")
sealed class PendingMessageContentLocalDto {

    @Serializable
    @SerialName("text")
    data class Text(val text: String) : PendingMessageContentLocalDto()

    @Serializable
    @SerialName("image")
    data class Image(val bytes: ByteArray) : PendingMessageContentLocalDto()

    @Serializable
    @SerialName("audio")
    data class Audio(
        val bytes: ByteArray,
        val durationMs: Long
    ) : PendingMessageContentLocalDto()

    @Serializable
    @SerialName("ayah")
    data class Ayah(
        val surahId: Int,
        val ayahNumber: Int,
        val ayahText: String
    ) : PendingMessageContentLocalDto()

    @Serializable
    @SerialName("money")
    data class Money(val amount: Double) : PendingMessageContentLocalDto()


    @Serializable
    @SerialName("order")
    data class Order(
        val orderId: String,
        val totalProducts: Int,
        val totalPrice: Double,
        val deliverToAddress: String
    ) : PendingMessageContentLocalDto()
}
