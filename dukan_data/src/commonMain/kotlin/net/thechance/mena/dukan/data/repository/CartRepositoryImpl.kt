package net.thechance.mena.dukan.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import net.thechance.mena.dukan.data.mapper.toDto
import net.thechance.mena.dukan.data.util.constants.EndPoints.CART_BASE_PATH
import net.thechance.mena.dukan.data.util.network.safeApiCall
import net.thechance.mena.dukan.domain.model.UpdateProductCartQuantityParams
import net.thechance.mena.dukan.domain.repository.CartRepository

class CartRepositoryImpl (
    private val client: HttpClient
): CartRepository {

    override suspend fun updateProductQuantity(params: UpdateProductCartQuantityParams) {
        safeApiCall<Unit> {
            client.put("${CART_BASE_PATH}/items") {
                contentType(ContentType.Application.Json)
                setBody(params.toDto())
            }
        }
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