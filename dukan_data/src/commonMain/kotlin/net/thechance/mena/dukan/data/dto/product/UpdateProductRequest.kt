package net.thechance.mena.dukan.data.dto.product

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProductRequest(
    @SerialName("name")
    val name: String?,
    @SerialName("description")
    val description: String?,
    @SerialName("price")
    val price: Double?,
    @SerialName("shelfId")
    val shelfId: String?,
    @SerialName("imageUrls")
    val imageUrls: List<String>?
)
