package net.thechance.mena.core_chat.data.messagesender

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.util.reflect.typeInfo
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.core_chat.data.source.remote.dto.MessageDto
import net.thechance.mena.core_chat.data.source.remote.network.HttpClientHolder
import net.thechance.mena.core_chat.data.source.remote.network.tryNetworkCall
import net.thechance.mena.core_chat.data.utils.buildAudioMultiPartFormData
import net.thechance.mena.core_chat.data.utils.now
import net.thechance.mena.core_chat.domain.entity.AudioData
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.domain.exception.SendMessageFailedException
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class AudioMessageSender(
    private val clientHolder: HttpClientHolder
) : MessageSender {

    private val client: HttpClient
        get() = clientHolder.getClient()

    override suspend fun send(message: Message) {
        val content = message.content

        if (content !is MessageContent.Audio) {
            throw SendMessageFailedException("Message content is not audio")
        }

        val bytes = (content.data as? AudioData.AudioByteArray)?.byteArray
            ?: throw SendMessageFailedException("Corrupted audio")

        val audioFile = ("audio_${LocalDateTime.now()}" to bytes)
        val multipartAudio = audioFile.buildAudioMultiPartFormData(
            fieldName = AUDIO_FILE_PARAM,
            messageId = message.id.toString(),
            chatId = message.chatId.toString(),
            audioDurationMs = content.audioDurationMs
        )

        tryNetworkCall<MessageDto>(
            bodyType = typeInfo<MessageDto>()
        ) {
            client.post(AUDIO_ENDPOINT) {
                setBody(multipartAudio)
            }
        }
    }

    companion object {
        const val AUDIO_ENDPOINT = "/chat/audio"
        const val AUDIO_FILE_PARAM = "audio"
    }
}