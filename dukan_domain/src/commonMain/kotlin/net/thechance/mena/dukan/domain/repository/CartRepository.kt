package net.thechance.mena.dukan.domain.repository

import net.thechance.mena.dukan.domain.entity.Cart

interface CartRepository {
    suspend fun getCartInfo(dukanId: String): Cart
}