package net.thechance.mena.dukan.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import net.thechance.mena.dukan.data.dto.PageResponseDto
import net.thechance.mena.dukan.data.dto.product.ProductCartDto
import net.thechance.mena.dukan.data.dto.product.toProductCart
import net.thechance.mena.dukan.data.mapper.toDomain
import net.thechance.mena.dukan.data.util.constants.EndPoints.PRODUCT_BASE_PATH
import net.thechance.mena.dukan.data.util.network.safeApiCall
import net.thechance.mena.dukan.domain.entity.Cart
import net.thechance.mena.dukan.domain.entity.ProductCart
import net.thechance.mena.dukan.domain.repository.CartProductsRepository
import net.thechance.mena.dukan.domain.util.PagedResult
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CartProductsRepositoryImpl(
    private val client: HttpClient
) : CartProductsRepository {

    override suspend fun getCartProducts(
        dukanId: Uuid,
        page: Int,
        size: Int
    ): PagedResult<ProductCart> {
        return safeApiCall<PageResponseDto<ProductCartDto>> {
            client.post("$PRODUCT_BASE_PATH/${dukanId}/items")
        }.toDomain(mapper = ProductCartDto::toProductCart)
    }

    override suspend fun getCartInfo(): Cart {
        TODO("Not yet implemented")
    }

}