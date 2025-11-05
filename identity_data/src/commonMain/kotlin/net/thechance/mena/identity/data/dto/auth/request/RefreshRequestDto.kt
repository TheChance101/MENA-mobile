package net.thechance.mena.identity.data.dto.auth.request

import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequestDto(
    val refreshToken: String
)