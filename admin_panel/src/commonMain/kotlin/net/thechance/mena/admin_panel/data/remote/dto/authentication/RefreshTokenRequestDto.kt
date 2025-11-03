package net.thechance.mena.admin_panel.data.remote.dto.authentication

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequestDto(
    @SerialName("refreshToken")
    val refreshToken: String
)
