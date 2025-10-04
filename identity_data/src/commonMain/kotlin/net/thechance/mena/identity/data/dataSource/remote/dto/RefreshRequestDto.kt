package net.thechance.mena.identity.data.dataSource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequestDto(
    val refreshToken: String
)