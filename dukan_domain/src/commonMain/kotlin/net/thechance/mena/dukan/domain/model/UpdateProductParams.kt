package net.thechance.mena.dukan.domain.model

data class UpdateProductParams(
    val name: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val shelfId: String? = null,
    val imageUrls: List<String>? = null,
)



