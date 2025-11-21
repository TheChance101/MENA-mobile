@file:OptIn(ExperimentalUuidApi::class)
package net.thechance.mena.core_chat.data.messagesender

import kotlinx.serialization.json.Json
import net.thechance.mena.core_chat.data.source.remote.dto.AyahMessageDto
import net.thechance.mena.core_chat.data.source.remote.network.WebSocketManager
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.domain.exception.SendMessageFailedException
import kotlin.uuid.ExperimentalUuidApi

class AyahMessageSender(
    private val webSocketManager: WebSocketManager,
    private val json: Json
) : MessageSender {

    override suspend fun send(message: Message) {
        if (!webSocketManager.isConnected()) {
            throw SendMessageFailedException("WebSocket is not connected")
        }

        val content = message.content
        if (content !is MessageContent.Ayah) {
            throw SendMessageFailedException("Message content is not Ayah")
        }

        val dto = AyahMessageDto(
            messageId = message.id.toString(),
            chatId = message.chatId.toString(),
            ayahNumber = content.ayahNumber,
            surahNumber = content.surahId,
            ayahContent = content.ayahContent
        )

        val messageJson = json.encodeToString(AyahMessageDto.serializer(), dto)
        webSocketManager.sendTextFrame(SEND_AYAH_DESTINATION, messageJson)
    }

    companion object {
        const val SEND_AYAH_DESTINATION = "/app/chat.privateAyahMessage"
    }
}