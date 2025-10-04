package net.thechance.mena.dukan.domain.util

data class CreateProductParams(
    val name: String,
    val description: String,
    val price: Double,
    val shelfId: String
)