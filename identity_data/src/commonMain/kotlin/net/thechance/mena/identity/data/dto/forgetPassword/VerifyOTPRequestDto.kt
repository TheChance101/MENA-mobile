package net.thechance.mena.identity.data.dto.forgetPassword

import kotlinx.serialization.Serializable

@Serializable
class VerifyOTPRequestDto (
    val otpCode: String,
    val sessionId: String
)