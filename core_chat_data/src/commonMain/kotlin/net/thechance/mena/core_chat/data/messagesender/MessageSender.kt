@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.data.messagesender

import net.thechance.mena.core_chat.domain.entity.Message
import kotlin.uuid.ExperimentalUuidApi

interface MessageSender {
    suspend fun send(message: Message)
}