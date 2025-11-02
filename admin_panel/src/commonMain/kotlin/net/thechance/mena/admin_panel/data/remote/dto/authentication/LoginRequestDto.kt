package net.thechance.mena.admin_panel.data.remote.dto.authentication

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    @SerialName("username")
    val userName: String,
    @SerialName("password")
    val password: String
)