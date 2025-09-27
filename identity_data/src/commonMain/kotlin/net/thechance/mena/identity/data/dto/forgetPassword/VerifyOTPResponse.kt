package net.thechance.mena.identity.data.dto.forgetPassword

import kotlinx.serialization.Serializable

@Serializable
data class VerifyOTPResponse(
    val message: String
)
