package net.thechance.mena.dukan.data.dto.shelf

import kotlinx.serialization.Serializable

@Serializable
data class UpdateShelfNameRequest(
    val title: String
)