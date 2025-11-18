package net.thechance.mena.identity.data.dto.privacyAndPolicy.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContactInfoResponseDto(
    @SerialName("email")
    val email: String,
    @SerialName("phoneNumber")
    val phoneNumber: String,
    @SerialName("facebookAccount")
    val facebookAccount: String,
)