package net.thechance.mena.identity.data.dto.auth.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    @SerialName("phoneNumber")
    val phoneNumber: String,
    @SerialName("password")
    val password: String
)
