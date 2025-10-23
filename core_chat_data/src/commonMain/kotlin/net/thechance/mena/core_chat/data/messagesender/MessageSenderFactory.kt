package net.thechance.mena.core_chat.data.messagesender

import net.thechance.mena.core_chat.domain.entity.MessageContent

class MessageSenderFactory(
    private val textMessageSender: TextMessageSender,
    private val imageMessageSender: ImageMessageSender
) {
    fun create(content: MessageContent) = when (content) {
        is MessageContent.Text -> textMessageSender
        is MessageContent.Images -> imageMessageSender
    }
}