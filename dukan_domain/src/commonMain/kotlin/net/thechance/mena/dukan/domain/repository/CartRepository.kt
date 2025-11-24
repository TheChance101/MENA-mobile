package net.thechance.mena.dukan.domain.repository

import net.thechance.mena.dukan.domain.entity.Cart
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.model.CheckoutParams
import net.thechance.mena.dukan.domain.model.Transaction
import net.thechance.mena.dukan.domain.model.UpdateProductCartQuantityParams
import net.thechance.mena.dukan.domain.util.PagedResult
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface CartRepository {
    suspend fun getCartInfo(dukanId: String): Cart

    suspend fun updateProductQuantity(params: UpdateProductCartQuantityParams)

    suspend fun addProductQuantity(params: UpdateProductCartQuantityParams)

    suspend fun deleteProductFromCart(dukanId: String, productId: String)

    suspend fun getCartProducts(dukanId: Uuid, page: Int, size: Int): PagedResult<Product>

    suspend fun checkout(params: CheckoutParams): Transaction
}