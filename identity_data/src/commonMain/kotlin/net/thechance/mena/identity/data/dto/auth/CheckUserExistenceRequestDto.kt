package net.thechance.mena.identity.data.dto.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheckUserExistenceRequestDto(
    @SerialName("username")
    val username: String
)