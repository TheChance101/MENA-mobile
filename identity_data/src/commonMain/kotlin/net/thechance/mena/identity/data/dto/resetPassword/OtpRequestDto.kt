package net.thechance.mena.identity.data.dto.resetPassword

import kotlinx.serialization.Serializable

@Serializable
data class OtpRequestDto(
    val phoneNumber: String,
    val defaultRegion: String
)