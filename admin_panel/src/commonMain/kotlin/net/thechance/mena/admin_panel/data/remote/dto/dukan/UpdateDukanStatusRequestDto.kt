package net.thechance.mena.admin_panel.data.remote.dto.dukan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateDukanStatusRequestDto(
    @SerialName("status")
    val status: String,
    @SerialName("message")
    val message: String
)