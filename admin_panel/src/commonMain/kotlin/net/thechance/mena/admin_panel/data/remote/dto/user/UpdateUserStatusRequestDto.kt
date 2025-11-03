package net.thechance.mena.admin_panel.data.remote.dto.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserStatusRequestDto(
    @SerialName("status")
    val status: String
)
