package net.thechance.mena.dukan.data.dto.order

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PriceDto(
    @SerialName("original")
    val original: Double,
    @SerialName("final")
    val finalPrice: Double
)