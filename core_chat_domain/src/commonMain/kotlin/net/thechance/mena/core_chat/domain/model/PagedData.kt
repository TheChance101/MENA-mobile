package net.thechance.mena.core_chat.domain.model

data class PagedData<T>(
    val data: List<T>,
    val totalItems: Int,
    val isLastPage: Boolean,
)