package net.thechance.mena.dukan.data.mapper

import net.thechance.mena.dukan.data.dto.product.PriceDto
import net.thechance.mena.dukan.domain.entity.Price

fun PriceDto.toDomain() : Price{
    return Price(
        base = this.base,
        final = this.final,
    )
}

fun Price.toRequest() : PriceDto{
    return PriceDto(
        base = this.base,
        final = this.final,
    )
}