package net.thechance.mena.admin_panel.data.mapper

import net.thechance.mena.admin_panel.data.remote.dto.DukanPagedResponse
import net.thechance.mena.admin_panel.data.remote.dto.PagedResponse
import net.thechance.mena.admin_panel.domain.model.PagedResult
import kotlin.collections.orEmpty

fun <DTO, ENTITY> PagedResponse<DTO>.toEntityPagedResult(
    mapper: DTO.() -> ENTITY
): PagedResult<ENTITY> {
    return PagedResult(
        items = items?.map { it.mapper() }.orEmpty(),
        totalPages = totalPages ?: 0,
        currentPage = page ?: 0,
        totalElements = totalElements ?: 0
    )
}

fun <DTO, ENTITY> DukanPagedResponse<DTO>.toEntityPagedResult(
    mapper: DTO.() -> ENTITY
): PagedResult<ENTITY> {
    return PagedResult(
        items = content?.map { it.mapper() }.orEmpty(),
        totalPages = totalPages ?: 0,
        currentPage = number ?: 0,
        totalElements = totalElements ?: 0
    )
}