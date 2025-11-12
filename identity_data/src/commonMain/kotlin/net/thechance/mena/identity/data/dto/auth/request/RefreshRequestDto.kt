package net.thechance.mena.identity.data.dto.auth.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequestDto(
    @SerialName("refreshToken")
    val refreshToken: String
)