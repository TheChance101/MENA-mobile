package net.thechance.mena.dukan.data.dto.product

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProductRequest(
    @SerialName("name")
    val name: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("price")
    val price: Double? = null,
    @SerialName("shelfId")
    val shelfId: String? = null,
    @SerialName("imageUrls")
    val imageUrls: List<String>? = null
)

