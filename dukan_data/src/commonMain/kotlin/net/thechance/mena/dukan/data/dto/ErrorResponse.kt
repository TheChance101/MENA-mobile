package net.thechance.mena.dukan.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val message: String,
    val errors: Map<String, String>? = null,
    val errorCode: Int? = null
)