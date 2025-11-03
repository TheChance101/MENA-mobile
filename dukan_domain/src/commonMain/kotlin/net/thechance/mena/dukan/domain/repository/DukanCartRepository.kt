package net.thechance.mena.dukan.domain.repository

import net.thechance.mena.dukan.domain.entity.Cart
import net.thechance.mena.dukan.domain.model.UpdateProductCartQuantityParams

interface DukanCartRepository {
    suspend fun getCartInfo(dukanId: String): Cart

    suspend fun updateProductQuantity(params: UpdateProductCartQuantityParams)

    suspend fun addProductQuantity(params: UpdateProductCartQuantityParams)

    suspend fun deleteProductFromCart(dukanId: String,productId: String)

}