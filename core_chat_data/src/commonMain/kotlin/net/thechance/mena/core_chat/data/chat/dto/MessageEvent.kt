package net.thechance.mena.core_chat.data.chat.dto

import kotlinx.serialization.Serializable
import net.thechance.mena.core_chat.data.chat.utils.MessageEventSerializer


@Serializable(with = MessageEventSerializer::class)
sealed class MessageEvent {
    data class Message(val dto: MessageDto) : MessageEvent()
    data class MarkAsRead(val dto: MarkAsReadResponse) : MessageEvent()
}