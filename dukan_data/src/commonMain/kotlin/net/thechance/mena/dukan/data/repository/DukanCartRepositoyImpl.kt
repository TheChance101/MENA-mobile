package net.thechance.mena.dukan.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import net.thechance.mena.dukan.data.dto.cart.CartDto
import net.thechance.mena.dukan.data.mapper.toDomain
import net.thechance.mena.dukan.data.util.constants.EndPoints.CART_BASE_PATH
import net.thechance.mena.dukan.data.util.network.safeApiCall
import net.thechance.mena.dukan.domain.entity.Cart
import net.thechance.mena.dukan.domain.repository.CartRepository

class DukanCartRepositoyImpl(
    private val client: HttpClient
) : CartRepository {
    override suspend fun getCartInfo(dukanId: String): Cart {
        return safeApiCall<CartDto> {
            client.get("$CART_BASE_PATH/$dukanId/info")
        }.toDomain()
    }
}