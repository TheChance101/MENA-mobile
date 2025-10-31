package net.thechance.mena.admin_panel.data.remote.dto

data class PagedResponse<T>(
    val totalElements: Long,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int,
    val items: List<T>
)