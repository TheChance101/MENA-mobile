package net.thechance.mena.dukan.presentation.component.pagination

data class PagingConfig(
    val pageSize: Int = 20,
    val prefetchDistance: Int = pageSize,
    val enablePlaceholders: Boolean = false,
    val maxSize: Int = Int.MAX_VALUE
)