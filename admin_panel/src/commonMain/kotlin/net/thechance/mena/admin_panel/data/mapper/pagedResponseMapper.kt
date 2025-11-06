package net.thechance.mena.admin_panel.data.mapper

import net.thechance.mena.admin_panel.data.remote.dto.PagedResponse
import net.thechance.mena.admin_panel.domain.model.PagedResult
import kotlin.collections.orEmpty

fun <DTO, ENTITY> PagedResponse<DTO>.toEntityPagedResult(
    mapper: DTO.() -> ENTITY
): PagedResult<ENTITY> {
    return PagedResult(
        items = items?.map { it.mapper() }.orEmpty(),
        totalPages = totalPages ?: 0,
        currentPage = page ?: 0
    )
}
