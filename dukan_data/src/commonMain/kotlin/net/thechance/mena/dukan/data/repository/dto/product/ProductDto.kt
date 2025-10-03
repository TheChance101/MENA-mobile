package net.thechance.mena.dukan.data.repository.dto.product

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ProductDto(
    @SerialName("id")
    val id: String,

    @SerialName("name")
    val name: String,

    @SerialName("shelfId")
    val shelfId: String,

    @SerialName("price")
    val price: Double,

    @SerialName("description")
    val description: String,

    @SerialName("imageUrls")
    val imageUrls: List<String>,

    @SerialName("createdAt")
    val createdAt: String
)