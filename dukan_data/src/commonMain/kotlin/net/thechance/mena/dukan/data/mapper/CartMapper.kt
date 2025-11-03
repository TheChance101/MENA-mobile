package net.thechance.mena.dukan.data.mapper

import net.thechance.mena.dukan.data.dto.cart.CartDto
import net.thechance.mena.dukan.domain.entity.Cart
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun CartDto.toDomain() = Cart(
    id = id,
    totalPrice = totalPrice
)