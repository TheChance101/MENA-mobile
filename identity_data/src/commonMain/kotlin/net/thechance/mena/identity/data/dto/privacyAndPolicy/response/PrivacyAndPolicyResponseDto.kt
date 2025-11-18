package net.thechance.mena.identity.data.dto.privacyAndPolicy.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrivacyAndPolicyResponseDto (
    @SerialName("updatedAt")
    val updateDate: String?,
    @SerialName("sections")
    val sections: List<SectionResponseDto>?

)
@Serializable
data class SectionResponseDto(
    @SerialName("title")
    val title: String,
    @SerialName("content")
    val content: String,
)