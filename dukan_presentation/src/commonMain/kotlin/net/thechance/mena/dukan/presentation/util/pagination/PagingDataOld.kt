package net.thechance.mena.dukan.presentation.util.pagination

data class PagingDataOld<T>(
    val items: List<T> = emptyList(),
    val isLoading: Boolean = false,
    val error: Exception? = null,
    val hasMore: Boolean = true,
    val isRefreshing: Boolean = false,
)