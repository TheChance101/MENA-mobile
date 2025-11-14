package net.thechance.mena.core_chat.domain.event

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class DeleteChatEvent(
    val chatId: Uuid
)