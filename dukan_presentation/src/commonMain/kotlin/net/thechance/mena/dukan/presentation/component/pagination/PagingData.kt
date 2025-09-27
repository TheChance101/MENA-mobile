package net.thechance.mena.dukan.presentation.component.pagination

data class PagingData<T>(
    val items: List<T> = emptyList(),
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val hasMore: Boolean = true,
    val isRefreshing: Boolean = false
)