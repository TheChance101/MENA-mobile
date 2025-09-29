package net.thechance.mena.identity.data.dto.resetPassword

import kotlinx.serialization.Serializable

@Serializable
data class OTPResponse(
    val sessionId: String
)
