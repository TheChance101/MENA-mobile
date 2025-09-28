package net.thechance.mena.identity.data.dto.forgetPassword

import kotlinx.serialization.Serializable
import net.thechance.mena.identity.domain.entity.PhoneNumber

@Serializable
class VerifyOTPRequestDto (
    val otp: String,
    val phoneNumber: String,
    val sessionId: String
)