package net.thechance.mena.identity.data.dto.resetPassword.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class VerifyOtpRequestDto (
    @SerialName("otp")
    val otp: String,
    @SerialName("sessionId")
    val sessionId: String
)