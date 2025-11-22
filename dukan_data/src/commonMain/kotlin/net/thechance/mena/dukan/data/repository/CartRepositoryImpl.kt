package net.thechance.mena.dukan.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import net.thechance.mena.dukan.data.dto.PageResponseDto
import net.thechance.mena.dukan.data.dto.cart.CartDto
import net.thechance.mena.dukan.data.dto.cart.TransactionDto
import net.thechance.mena.dukan.data.dto.product.ProductCartDto
import net.thechance.mena.dukan.data.mapper.toDomain
import net.thechance.mena.dukan.data.mapper.toDto
import net.thechance.mena.dukan.data.util.constants.EndPoints.CART_BASE_PATH
import net.thechance.mena.dukan.data.util.network.safeApiCall
import net.thechance.mena.dukan.domain.entity.Cart
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.model.CheckoutParams
import net.thechance.mena.dukan.domain.model.Transaction
import net.thechance.mena.dukan.domain.model.UpdateProductCartQuantityParams
import net.thechance.mena.dukan.domain.repository.CartRepository
import net.thechance.mena.dukan.domain.util.PagedResult
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CartRepositoryImpl(
    private val client: HttpClient
) : CartRepository {
    override suspend fun getCartInfo(dukanId: String): Cart {
        return safeApiCall<CartDto> {
            client.get("$CART_BASE_PATH/$dukanId/info")
        }.toDomain()
    }

    override suspend fun updateProductQuantity(params: UpdateProductCartQuantityParams) {
        safeApiCall<Unit> {
            client.put("${CART_BASE_PATH}/items") {
                contentType(ContentType.Application.Json)
                setBody(params.toDto())
            }
        }
    }

    override suspend fun getCartProducts(
        dukanId: Uuid,
        page: Int,
        size: Int
    ): PagedResult<Product> {
        return safeApiCall<PageResponseDto<ProductCartDto>> {
            client.get("$CART_BASE_PATH/${dukanId}/items")
        }.toDomain(mapper = ProductCartDto::toDomain)
    }

    override suspend fun checkout(params: CheckoutParams): Transaction {
        return safeApiCall<TransactionDto> {
            client.post("$CART_BASE_PATH/checkout") {
                contentType(ContentType.Application.Json)
                setBody(params.toDto())
            }
        }.toDomain()
    }

    override suspend fun addProductQuantity(params: UpdateProductCartQuantityParams) {
        safeApiCall<Unit> {
            client.post("${CART_BASE_PATH}/items") {
                contentType(ContentType.Application.Json)
                setBody(params.toDto())
            }
        }
    }

    override suspend fun deleteProductFromCart(dukanId: String, productId: String) {
        safeApiCall<Unit> {
            client.delete("${CART_BASE_PATH}/$dukanId/items/$productId")
        }
    }
}
