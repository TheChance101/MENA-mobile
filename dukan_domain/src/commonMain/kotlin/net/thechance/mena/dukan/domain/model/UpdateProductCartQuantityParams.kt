package net.thechance.mena.dukan.domain.model

data class UpdateProductCartQuantityParams(
    val dukanId: String,
    val productId: String,
    val quantity: Int
)