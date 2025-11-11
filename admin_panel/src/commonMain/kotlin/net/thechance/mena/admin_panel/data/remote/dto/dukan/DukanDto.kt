package net.thechance.mena.admin_panel.data.remote.dto.dukan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.thechance.mena.admin_panel.domain.entity.dukan.Dukan

@Serializable
data class DukanDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String? = null,
    @SerialName("imageUrl")
    val imageUrl: String? = null,
    @SerialName("address")
    val address: String? = null,
    @SerialName("latitude")
    val latitude: Double? = null,
    @SerialName("longitude")
    val longitude: Double? = null,
    @SerialName("color")
    val color: ColorDto? = null,
    @SerialName("categories")
    val categories: List<CategoryDto>? = null
)