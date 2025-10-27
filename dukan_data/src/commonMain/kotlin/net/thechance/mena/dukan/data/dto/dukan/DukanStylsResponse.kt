package net.thechance.mena.dukan.data.dto.dukan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DukanStylesResponse(
    @SerialName("styles")
    val styles: List<String>,
)
