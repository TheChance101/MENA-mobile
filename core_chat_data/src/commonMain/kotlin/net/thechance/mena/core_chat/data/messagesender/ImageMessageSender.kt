@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.data.messagesender

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.util.reflect.typeInfo
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.core_chat.data.source.remote.dto.MessageDto
import net.thechance.mena.core_chat.data.source.remote.network.HttpClientHolder
import net.thechance.mena.core_chat.data.utils.buildImageMultiPartFormData
import net.thechance.mena.core_chat.data.utils.now
import net.thechance.mena.core_chat.data.utils.tryNetworkCall
import net.thechance.mena.core_chat.domain.entity.ImageData
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.domain.exception.SendMessageFailedException
import kotlin.uuid.ExperimentalUuidApi

class ImageMessageSender(
    private val clientHolder: HttpClientHolder,
) : MessageSender {
    private val client: HttpClient
        get() = clientHolder.getClient()

    override suspend fun send(message: Message) {
        val content = message.content

        if (content !is MessageContent.Image) {
            throw SendMessageFailedException("Failed to send message: Message content is not images")
        }

        val data = content.data

        val bytes = if (data is ImageData.ImageByteArray) {
            data.byteArray
        } else {
            throw SendMessageFailedException("Failed to send message: Corrupted images")
        }

        val imageFile = ("image_${LocalDateTime.now()}" to bytes)
        val multipartImage = imageFile.buildImageMultiPartFormData(
            fieldName = IMAGES_FILES_PARAM,
            messageId = message.id.toString(),
            chatId = message.chatId.toString()
        )

        tryNetworkCall<MessageDto>(
            bodyType = typeInfo<MessageDto>()
        ) {
            client.post(IMAGES_ENDPOINT) {
                setBody(multipartImage)
            }
        }

    }

    companion object {
        const val IMAGES_ENDPOINT = "/chat/image"
        const val IMAGES_FILES_PARAM = "image"
    }
}