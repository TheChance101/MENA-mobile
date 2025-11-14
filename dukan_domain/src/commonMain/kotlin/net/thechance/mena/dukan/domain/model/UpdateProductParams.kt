package net.thechance.mena.dukan.domain.model

import net.thechance.mena.dukan.domain.entity.Price

data class UpdateProductParams(
    val name: String?,
    val description: String?,
    val price: Price,
    val shelfId: String?,
    val imageUrls: List<String>?,
    val isOutOfStock: Boolean = false,
)



