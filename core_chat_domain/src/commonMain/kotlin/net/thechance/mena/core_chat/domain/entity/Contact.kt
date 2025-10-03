package net.thechance.mena.core_chat.domain.entity

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@ExperimentalUuidApi
data class Contact(
    val firstName: String,
    val lastName: String,
    val phone: String,
    val menaUserId: Uuid?,
    val imageUrl: String?,
)