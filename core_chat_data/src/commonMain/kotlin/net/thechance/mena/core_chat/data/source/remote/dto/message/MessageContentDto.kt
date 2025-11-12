package net.thechance.mena.core_chat.data.source.remote.dto.message

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Polymorphic
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

}