package net.thechance.mena.dukan.data.dto.shelf

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateShelfRequest(
    @SerialName("title")
    val title: String
)