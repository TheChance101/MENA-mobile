package net.thechance.mena.admin_panel.domain.entity.dukan

import kotlinx.datetime.LocalDateTime

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val discountedPrice: Double,
    val description: String,
    val imageUrls: List<String?>,
    val createdAt: LocalDateTime
)