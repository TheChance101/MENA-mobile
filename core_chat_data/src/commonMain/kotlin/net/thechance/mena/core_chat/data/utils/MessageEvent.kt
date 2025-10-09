package net.thechance.mena.core_chat.data.utils

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
import net.thechance.mena.core_chat.data.source.remote.dto.MarkAsReadResponse
import net.thechance.mena.core_chat.data.source.remote.dto.MessageDto


@Serializable(with = MessageEventSerializer::class)
sealed class MessageEvent {
    data class Message(val dto: MessageDto) : MessageEvent()
    data class MarkAsRead(val dto: MarkAsReadResponse) : MessageEvent()
}

private object MessageEventSerializer : KSerializer<MessageEvent> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("WebSocketEvent")

    override fun serialize(encoder: Encoder, value: MessageEvent) {
        when (value) {
            is MessageEvent.Message ->
                encoder.encodeSerializableValue(MessageDto.serializer(), value.dto)

            is MessageEvent.MarkAsRead ->
                encoder.encodeSerializableValue(MarkAsReadResponse.serializer(), value.dto)
        }
    }

    override fun deserialize(decoder: Decoder): MessageEvent {
        val element = decoder.decodeSerializableValue(JsonElement.serializer()).jsonObject

        return when {
            "readBy" in element ->
                MessageEvent.MarkAsRead(
                    Json.decodeFromJsonElement(
                        MarkAsReadResponse.serializer(),
                        element
                    )
                )

            "id" in element && "senderId" in element && "chatId" in element && "sendAt" in element && "isRead" in element ->
                MessageEvent.Message(Json.decodeFromJsonElement(MessageDto.serializer(), element))

            else -> throw SerializationException("Unknown payload: $element")
        }
    }
}