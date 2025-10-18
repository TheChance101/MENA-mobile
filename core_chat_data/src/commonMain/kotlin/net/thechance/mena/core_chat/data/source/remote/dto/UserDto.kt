package net.thechance.mena.core_chat.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class UserDto (
    @SerialName("firstName") val firstName: String?,
    @SerialName("lastName") val lastName: String?,
    @SerialName("imageUrl") val imageUrl: String?,
    @SerialName("phoneNumber") val phoneNumber: String?,
    )