package net.thechance.mena.dukan.presentation.util.pagination

data class PagingConfig(
    val pageSize: Int = 10,
    val prefetchDistance: Int = 1,
    val enablePlaceholders: Boolean = false,
    val maxSize: Int = Int.MAX_VALUE
)