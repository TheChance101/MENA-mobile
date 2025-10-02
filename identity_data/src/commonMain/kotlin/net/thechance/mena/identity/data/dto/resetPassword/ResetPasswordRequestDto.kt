package net.thechance.mena.identity.data.dto.resetPassword

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequestDto(
    val newPassword: String,
    val confirmPassword: String,
    val phoneNumber: String,
)