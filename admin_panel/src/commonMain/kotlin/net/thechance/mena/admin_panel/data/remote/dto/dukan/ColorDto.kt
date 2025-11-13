package net.thechance.mena.admin_panel.data.remote.dto.dukan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ColorDto(
    @SerialName("id")
    val id: String,
    @SerialName("hexCode")
    val hexCode: String? = null
)