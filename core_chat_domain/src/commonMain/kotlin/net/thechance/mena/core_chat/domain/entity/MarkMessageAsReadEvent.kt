package net.thechance.mena.core_chat.domain.entity

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class MarkMessageAsReadEvent(
    val readByUserId: Uuid,
    val chatId: Uuid,
    val readByMe: Boolean
)
