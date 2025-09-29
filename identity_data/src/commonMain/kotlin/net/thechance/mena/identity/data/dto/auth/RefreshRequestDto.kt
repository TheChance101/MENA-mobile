package net.thechance.mena.identity.data.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequestDto(
    val refreshToken: String
)