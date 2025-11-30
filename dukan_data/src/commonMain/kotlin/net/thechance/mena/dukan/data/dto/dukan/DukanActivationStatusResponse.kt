package net.thechance.mena.dukan.data.dto.dukan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DukanActivationStatusResponse(
    @SerialName("activationStatus")
    val status: String,
    @SerialName("reason")
    val reason: String?
)