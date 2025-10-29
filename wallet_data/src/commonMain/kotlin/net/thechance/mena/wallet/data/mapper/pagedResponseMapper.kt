package net.thechance.mena.wallet.data.mapper

import net.thechance.mena.wallet.data.dto.remote.PagedResponse

fun <DTO, ENTITY> PagedResponse<DTO>.toEntityList(mapper: DTO.() -> ENTITY): List<ENTITY> {
    return this.items?.map{ it.mapper() }.orEmpty()
}