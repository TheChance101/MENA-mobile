package net.thechance.mena.dukan.domain.entity

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrls: List<String>,
    val createdAt: String
)