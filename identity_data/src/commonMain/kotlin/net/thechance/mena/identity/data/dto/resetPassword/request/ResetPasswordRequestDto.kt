package net.thechance.mena.identity.data.dto.resetPassword.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequestDto(
    @SerialName("newPassword")
    val newPassword: String,
    @SerialName("confirmPassword")
    val confirmPassword: String,
    @SerialName("sessionId")
    val sessionId: String
)