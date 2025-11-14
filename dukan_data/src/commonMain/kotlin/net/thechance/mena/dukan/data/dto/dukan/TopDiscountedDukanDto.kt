package net.thechance.mena.dukan.data.dto.dukan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class TopDiscountedDukanDto(

    @SerialName("imageUrl")
    val imageUrl: String,

    @SerialName("discount")
    val discount: Double,

    @SerialName("id")
    val id: Uuid
)