package net.thechance.mena.dukan.presentation.util.pagination


data class PagedFetchResponse<T>(
    val items: List<T>,
    val currentPage: Int,
    val totalPages: Int,
    val hasNext: Boolean = currentPage < totalPages,
    val hasPrevious: Boolean = currentPage > 1
)