package net.thechance.mena.identity.data.dataSource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val phoneNumber: String,
    val password: String
)
