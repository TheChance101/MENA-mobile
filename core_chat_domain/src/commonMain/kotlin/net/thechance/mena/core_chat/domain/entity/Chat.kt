package net.thechance.mena.core_chat.domain.entity

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@ExperimentalUuidApi
data class Chat(
    val id: Uuid,
    val name: String,
    val imageUrl:String?,
    val requesterId: Uuid,
    val receiverId: Uuid,
)
