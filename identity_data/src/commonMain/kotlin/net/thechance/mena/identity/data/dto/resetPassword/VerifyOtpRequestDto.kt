package net.thechance.mena.identity.data.dto.resetPassword

import kotlinx.serialization.Serializable

@Serializable
class VerifyOtpRequestDto (
    val otp: String,
    val phoneNumber: String,
    val sessionId: String
)