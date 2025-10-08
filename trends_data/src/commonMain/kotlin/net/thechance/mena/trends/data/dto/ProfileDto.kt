package net.thechance.mena.trends.data.dto


import kotlinx.serialization.SerialName

import kotlinx.serialization.Serializable
@Serializable
data class ProfileDto(
    @SerialName("username")
    val username: String,
    @SerialName("firstName")
    val firstName: String,
    @SerialName("lastName")
    val lastName: String,
    @SerialName("profileImageUrl")
    val profileImageUrl: String? = null

)