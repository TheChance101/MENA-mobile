package net.thechance.mena.dukan.domain.util

data class PagedResult<T>(
    val items: List<T>,
    val currentPage: Int,
    val totalItems: Long,
    val pageSize: Int,
    val totalPages: Int,
)