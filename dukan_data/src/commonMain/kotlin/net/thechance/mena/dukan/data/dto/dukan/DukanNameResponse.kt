package net.thechance.mena.dukan.data.dto.dukan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DukanNameResponse (
    @SerialName("available")
    val available: Boolean,
)