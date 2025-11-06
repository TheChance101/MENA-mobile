package net.thechance.mena.identity.data.dto.profile.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordResponseDto(
    @SerialName("message")
    val message:String
)
