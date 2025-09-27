package net.thechance.mena.identity.data.dto.forgetPassword

import kotlinx.serialization.Serializable

@Serializable
data class OTPResponse(
    val sessionId: String
)
