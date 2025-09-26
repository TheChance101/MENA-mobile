package net.thechance.mena.identity.data.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val phoneNumber: String,
    val password: String
)
