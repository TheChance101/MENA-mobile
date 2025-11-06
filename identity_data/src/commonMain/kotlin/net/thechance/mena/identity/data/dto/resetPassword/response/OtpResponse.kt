package net.thechance.mena.identity.data.dto.resetPassword.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OtpResponse(
    @SerialName("sessionId")
    val sessionId: String
)