@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.data.messagesender

import kotlinx.serialization.json.Json
import net.thechance.mena.core_chat.data.source.remote.dto.SendMessageDto
import net.thechance.mena.core_chat.data.source.remote.network.WebSocketManager
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.domain.exception.SendMessageFailedException
import kotlin.uuid.ExperimentalUuidApi

class TextMessageSender(
    private val webSocketManager: WebSocketManager,
    private val json: Json
): MessageSender {
    override suspend fun send(message: Message) {
        val content = message.content

        if (!webSocketManager.isConnected()) {
            throw SendMessageFailedException("WebSocket is not connected")
        }

        if (content !is MessageContent.Text) {
            throw SendMessageFailedException("Message content is not text")
        }

        val dto = SendMessageDto(messageId = message.id.toString(), chatId = message.chatId.toString(), text = content.text)
        val messageJson = json.encodeToString(SendMessageDto.serializer(), dto)
        webSocketManager.sendTextFrame(SEND_MESSAGE_DESTINATION, messageJson)
    }

    companion object {
        const val SEND_MESSAGE_DESTINATION = "/app/chat.privateMessage"

    }
}