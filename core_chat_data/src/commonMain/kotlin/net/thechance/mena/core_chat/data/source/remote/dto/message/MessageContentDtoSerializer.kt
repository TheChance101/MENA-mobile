package net.thechance.mena.core_chat.data.source.remote.dto.message

import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.serialization.json.put
object MessageContentDtoSerializer : KSerializer<MessageContentDto> {

    override val descriptor = JsonObject.serializer().descriptor

    override fun serialize(encoder: Encoder, value: MessageContentDto) {
        val jsonElement = when (value) {
            is MessageContentDto.Text -> buildJsonObject {
                put("text", value.text)
            }
            is MessageContentDto.Image -> buildJsonObject {
                put("url", value.url)
            }
            is MessageContentDto.Audio -> buildJsonObject {
                put("url", value.url)
                put("duration", value.duration)
            }
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): MessageContentDto {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return when {
            "text" in jsonObject -> MessageContentDto.Text(jsonObject["text"]!!.jsonPrimitive.content)
            "url" in jsonObject && "duration" in jsonObject -> MessageContentDto.Audio(
                url = jsonObject["url"]!!.jsonPrimitive.content,
                duration = jsonObject["duration"]!!.jsonPrimitive.long
            )
            "url" in jsonObject -> MessageContentDto.Image(jsonObject["url"]!!.jsonPrimitive.content)
            else -> throw IllegalArgumentException("Unknown MessageContentDto type: $jsonObject")
        }
    }
}