package net.thechance.mena.dukan.data.dto.product

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeleteProductImagesRequest(
    @SerialName("imageUrls")
    val imageUrls: List<String>
)

