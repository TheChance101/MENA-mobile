package net.thechance.mena.admin_panel.domain.entity.dukan

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val discountedPrice: Double?,
    val description: String,
    val imageUrls: List<String>,
)