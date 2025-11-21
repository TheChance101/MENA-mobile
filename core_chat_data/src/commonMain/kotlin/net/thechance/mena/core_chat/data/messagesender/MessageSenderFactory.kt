package net.thechance.mena.core_chat.data.messagesender

import net.thechance.mena.core_chat.domain.entity.MessageContent

class MessageSenderFactory(
    private val textMessageSender: TextMessageSender,
    private val imageMessageSender: ImageMessageSender,
    private val audioMessageSender: AudioMessageSender,
    private val ayahMessageSender: AyahMessageSender
) {
    fun create(content: MessageContent) = when (content) {
        is MessageContent.Text -> textMessageSender
        is MessageContent.Image -> imageMessageSender
        is MessageContent.Audio -> audioMessageSender
        is MessageContent.Ayah -> ayahMessageSender
    }
}