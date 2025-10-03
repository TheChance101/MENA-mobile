package net.thechance.mena.dukan.data.repository.mapper

import net.thechance.mena.dukan.data.repository.dto.PageResponseDto
import net.thechance.mena.dukan.domain.util.PagedResult

fun <T, R> PageResponseDto<T>.toDomain(mapper: (T) -> R): PagedResult<R> {
    return PagedResult(
        items = content.map(mapper),
        currentPage = number,
        totalPages = totalPages,
        hasNext = !last,
        hasPrevious = !first,
        totalItems = totalElements

    )
}