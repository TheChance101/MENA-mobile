package net.thechance.mena.admin_panel.data.remote.dto.dukan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    @SerialName("id")
    val id: String,
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("title")
    val title: String? = null
)