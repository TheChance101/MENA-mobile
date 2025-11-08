package net.thechance.mena.identity.data.dto.auth.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    @SerialName("phoneNumber")
    val phoneNumber: String,
    @SerialName("username")
    val username: String,
    @SerialName("firstName")
    val firstName: String,
    @SerialName("lastName")
    val lastName: String,
    @SerialName("birthDate")
    val birthDate: String,
    @SerialName("gender")
    val gender: Int,
    @SerialName("password")
    val password: String,
    @SerialName("sessionId")
    val sessionId: String
)