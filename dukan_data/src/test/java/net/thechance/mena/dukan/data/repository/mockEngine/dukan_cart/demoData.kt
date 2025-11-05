package net.thechance.mena.dukan.data.repository.mockEngine.dukan_cart

import net.thechance.mena.dukan.data.dto.cart.CartDto
import net.thechance.mena.dukan.data.mapper.toDomain
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
val cartDto = CartDto(
    id = Uuid.random(),
    totalPrice = 150.0
)

val cart1 = cartDto.toDomain()