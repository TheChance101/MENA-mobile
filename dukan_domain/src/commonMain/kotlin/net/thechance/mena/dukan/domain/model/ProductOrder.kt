package net.thechance.mena.dukan.domain.model

data class ProductOrder(
    val productId: String,
    val quantity: Int,
    val imageUrl:String,
    val name: String,
    val price: Double
)