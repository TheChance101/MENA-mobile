package net.thechance.mena.identity.data.dto.forgetPassword

import kotlinx.serialization.Serializable

@Serializable
data class OTPRequestDto(
    val phoneNumber: String,
    val countryCode: String
)