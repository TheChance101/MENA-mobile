package net.thechance.mena.identity.data.dto.profile.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequestDto(
    @SerialName("currentPassword")
    val currentPassword:String,
    @SerialName("newPassword")
    val newPassword:String,
    @SerialName("confirmPassword")
    val confirmPassword:String
)