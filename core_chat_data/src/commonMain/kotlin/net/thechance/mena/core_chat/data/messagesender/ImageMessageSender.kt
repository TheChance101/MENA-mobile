@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.data.messagesender

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.util.reflect.typeInfo
import net.thechance.mena.core_chat.data.source.local.database.MessageDao
import net.thechance.mena.core_chat.data.source.remote.dto.MessageDto
import net.thechance.mena.core_chat.data.utils.buildImageMultiPartFormData
import net.thechance.mena.core_chat.data.utils.tryNetworkCall
import net.thechance.mena.core_chat.domain.entity.ImageData
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.domain.exception.SendMessageFailedException
import kotlin.uuid.ExperimentalUuidApi

class ImageMessageSender(
    private val client: HttpClient,
    private val messageDao: MessageDao
) : MessageSender {
    override suspend fun send(message: Message) {
        val content = message.content

        if (content !is MessageContent.Images) {
            throw SendMessageFailedException("Failed to send message: Message content is not images")
        }

        val source = content.source

        val byteArrays = if (source is ImageData.ImageByteArray) {
            source.byteArrays
        } else {
            throw SendMessageFailedException("Failed to send message: Corrupted images")
        }

        val imageFiles = byteArrays.mapIndexed { index, bytes -> "image_$index" to bytes }
        var messageId: String? = null
        val remainingImages = byteArrays.toMutableList()

        for ((imageName, imageBytes) in imageFiles) {
            val multipart = (imageName to imageBytes).buildImageMultiPartFormData(
                fieldName = IMAGES_FILES_PARAM,
                chatId = message.chatId.toString(),
                messageId = messageId
            )

            val messageResponse = tryNetworkCall<MessageDto>(
                bodyType = typeInfo<MessageDto>()
            ) {
                client.post(IMAGES_ENDPOINT) {
                    setBody(multipart)
                }
            }

            if (messageResponse != null) {
                remainingImages.remove(imageBytes)
                messageDao.updateMessageImages(
                    id = message.id.toString(),
                    images = remainingImages
                )
                if (messageId == null) {
                    messageId = messageResponse.id
                }
            }
        }
    }

    companion object {
        const val IMAGES_ENDPOINT = "/chat/image"
        const val IMAGES_FILES_PARAM = "image"
    }
}