package net.thechance.mena.dukan.domain.repository

import net.thechance.mena.dukan.domain.entity.Cart
import net.thechance.mena.dukan.domain.entity.ProductCart
import net.thechance.mena.dukan.domain.util.PagedResult
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface CartRepository {
    suspend fun getCartProducts(dukanId: Uuid, page: Int, size: Int): PagedResult<ProductCart>
    suspend fun getCartInfo(): Cart
}