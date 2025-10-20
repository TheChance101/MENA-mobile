package net.thechance.mena.dukan.data.dto.shelf

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class ShelfDto(
    @SerialName("id")
    val id: Uuid,
    @SerialName("title")
    val title: String
)