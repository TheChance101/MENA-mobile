package net.thechance.mena.core_chat.presentation.api

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class ChatEntryState(
    val chatId: Uuid? = null,
    val chatName: String? = null,
    val isContentVisible: Boolean = false,
    val error: Boolean = false
)