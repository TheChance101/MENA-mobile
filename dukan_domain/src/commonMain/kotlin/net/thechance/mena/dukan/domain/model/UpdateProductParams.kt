package net.thechance.mena.dukan.domain.model

data class UpdateProductParams(
    val name: String?,
    val description: String?,
    val price: Double?,
    val shelfId: String?,
    val imageUrls: List<String>?,
    val isOutOfStock: Boolean = false,
)



