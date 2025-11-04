package net.thechance.mena.admin_panel.data.remote.dto.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    @SerialName("id")
    val id: String,
    @SerialName("firstName")
    val firstName: String?,
    @SerialName("phoneNumber")
    val phoneNumber: String?,
    @SerialName("lastName")
    val lastName: String?,
    @SerialName("lastLoginAt")
    val lastLoginAt: String?,
    @SerialName("lastVisitAt")
    val lastVisitAt: String?,
    @SerialName("status")
    val status: String?
)