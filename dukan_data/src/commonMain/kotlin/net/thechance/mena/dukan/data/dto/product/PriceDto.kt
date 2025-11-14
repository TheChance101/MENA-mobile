package net.thechance.mena.dukan.data.dto.product

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PriceDto(
    @SerialName("base")
    val base: Double,

    @SerialName("final")
    val final: Double? = null
)
