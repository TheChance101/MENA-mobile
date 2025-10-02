package net.thechance.mena.dukan.domain.util

data class PagedResult<T>(
    val items: List<T>,
    val currentPage: Int,
    val totalItems: Long,
    val totalPages: Int,
    val hasNext: Boolean = currentPage < totalPages,
    val hasPrevious: Boolean = currentPage > 1
)