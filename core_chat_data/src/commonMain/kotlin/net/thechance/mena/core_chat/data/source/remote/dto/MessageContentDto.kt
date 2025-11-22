@file:OptIn(ExperimentalSerializationApi::class)

package net.thechance.mena.core_chat.data.source.remote.dto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
@JsonClassDiscriminator("type")
sealed class MessageContentDto {

    @Serializable
    @SerialName("TEXT")
    data class Text(val text: String) : MessageContentDto()

    @Serializable
    @SerialName("IMAGE")
    data class Image(val url: String) : MessageContentDto()

    @Serializable
    @SerialName("AUDIO")
    data class Audio(val url: String, val duration: Long? = null) : MessageContentDto()

    @Serializable
    @SerialName("MONEY")
    data class Money(val amount: Double) : MessageContentDto()

    @Serializable
    @SerialName("AYAH")
    data class Ayah(
        @SerialName("suraNumber")
        val surahNumber: Int,
        @SerialName("ayahNumber")
        val ayahNumber: Int,
        @SerialName("ayahText")
        val ayahContent: String
    ) : MessageContentDto()

    @Serializable
    @SerialName("ORDER")
    data class Order(
        val orderId: String,
        val totalProducts: Int,
        val totalPrice: String,
        val deliverToAddress: String
    ) : MessageContentDto()

}