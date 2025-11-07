package net.thechance.mena.identity.data.dto.profile.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequestDto(
    @SerialName("username")
    val username: String,
    @SerialName("firstName")
    val firstName: String,
    @SerialName("lastName")
    val lastName: String,
    @SerialName("profileImageUrl")
    val imageUrl: String?,
    @SerialName("birthDate")
    val birthDate: String,
    @SerialName("gender")
    val gender: Int
)