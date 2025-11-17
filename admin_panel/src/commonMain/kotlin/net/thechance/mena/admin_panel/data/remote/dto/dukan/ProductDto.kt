package net.thechance.mena.admin_panel.data.remote.dto.dukan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String? = null,
    @SerialName("finalPrice")
    val finalPrice: Double? = null,
    @SerialName("basePrice")
    val basePrice: Double? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("imageUrls")
    val imageUrls: List<String>? = null,
    @SerialName("createdAt")
    val createdAt: String? = null
)