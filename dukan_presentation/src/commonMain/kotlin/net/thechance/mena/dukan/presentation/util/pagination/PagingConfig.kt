package net.thechance.mena.dukan.presentation.util.pagination

data class PagingConfig(
    val pageSize: Int = 10,
    val prefetchDistance: Int = pageSize / 2,
    val enablePlaceholders: Boolean = false,
    val maxSize: Int = Int.MAX_VALUE
)