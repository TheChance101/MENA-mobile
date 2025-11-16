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
    @SerialName("AYAH")
    data class Ayah(
        val suraNumber: Int,
        val ayahNumber: Int,
        val ayahText: String
    ) : MessageContentDto()
}