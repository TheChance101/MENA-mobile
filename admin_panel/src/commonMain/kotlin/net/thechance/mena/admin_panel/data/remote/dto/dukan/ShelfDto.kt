package net.thechance.mena.admin_panel.data.remote.dto.dukan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShelfDto(
    @SerialName("id")
    val id: String,
    @SerialName("title")
    val title: String? = null
)