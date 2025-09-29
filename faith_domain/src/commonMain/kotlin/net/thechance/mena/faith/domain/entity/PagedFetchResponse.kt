package net.thechance.mena.faith.domain.entity

data class PagedFetchResponse<T>(
    val currentPage: Int,
    val items: List<T>,
    val totalPages: Int,
    val totalItems: Int
)