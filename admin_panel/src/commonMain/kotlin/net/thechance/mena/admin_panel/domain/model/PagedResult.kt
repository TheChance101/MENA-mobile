package net.thechance.mena.admin_panel.domain.model

data class PagedResult<T>(
    val items: List<T>,
    val totalPages: Int,
    val currentPage: Int,
    val totalElements: Int
)
