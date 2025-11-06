package net.thechance.mena.identity.data.dto.resetPassword.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OtpRequestDto(
    @SerialName("phoneNumber")
    val phoneNumber: String,
    @SerialName("defaultRegion")
    val defaultRegion: String
)