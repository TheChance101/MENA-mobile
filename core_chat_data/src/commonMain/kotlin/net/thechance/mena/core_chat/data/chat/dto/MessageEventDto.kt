package net.thechance.mena.core_chat.data.chat.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject


@Serializable(with = MessageEventDtoSerializer::class)
sealed class MessageEventDto {
    data class Message(val dto: MessageRemoteDto) : MessageEventDto()
    data class MarkAsRead(val dto: MarkAsReadResponse) : MessageEventDto()
}

private object MessageEventDtoSerializer : KSerializer<MessageEventDto> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("WebSocketEvent")

    override fun serialize(encoder: Encoder, value: MessageEventDto) {
        when (value) {
            is MessageEventDto.Message ->
                encoder.encodeSerializableValue(MessageRemoteDto.serializer(), value.dto)

            is MessageEventDto.MarkAsRead ->
                encoder.encodeSerializableValue(MarkAsReadResponse.serializer(), value.dto)
        }
    }

    override fun deserialize(decoder: Decoder): MessageEventDto {
        val element = decoder.decodeSerializableValue(JsonElement.serializer()).jsonObject

        return when {
            "readBy" in element ->
                MessageEventDto.MarkAsRead(
                    Json.decodeFromJsonElement(
                        MarkAsReadResponse.serializer(),
                        element
                    )
                )

            "id" in element && "senderId" in element && "chatId" in element && "sendAt" in element && "isRead" in element ->
                MessageEventDto.Message(Json.decodeFromJsonElement(MessageRemoteDto.serializer(), element))

            else -> throw SerializationException("Unknown payload: $element")
        }
    }
}
