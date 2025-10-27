package net.thechance.mena.dukan.data.mapper

import net.thechance.mena.dukan.data.dto.PageResponseDto
import net.thechance.mena.dukan.domain.util.PagedResult

fun <T, R> PageResponseDto<T>.toDomain(mapper: (T) -> R): PagedResult<R> {
    return PagedResult(
        items = content.map(mapper),
        totalPages = totalPages,
        pageSize = size,
        currentPage = number,
        totalItems = totalElements
    )
}