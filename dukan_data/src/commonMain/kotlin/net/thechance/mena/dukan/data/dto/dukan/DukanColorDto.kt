package net.thechance.mena.dukan.data.dto.dukan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class DukanColorsResponse(
    @SerialName("colors")
    val colors: List<DukanColorDto>,
)

@Serializable
data class DukanColorDto @OptIn(ExperimentalUuidApi::class) constructor(
    @SerialName("id")
    val id: Uuid,
    @SerialName("hexCode")
    val hexCode: String,
)