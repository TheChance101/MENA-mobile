package net.thechance.mena.dukan.presentation.util.pagination.base

import net.thechance.mena.dukan.domain.util.PagedResult
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.util.pagination.PagingConfig
import net.thechance.mena.dukan.presentation.util.pagination.PagingSource

abstract class BasePagingSource<T : Any> : PagingSource<Int, T>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val page = params.key ?: FIRST_PAGE
            val fetchResponse = onFetchPage(pageNumber = page)

            LoadResult.Page(
                data = fetchResponse.items,
                prevKey = if (fetchResponse.hasPrevious) page - 1 else null,
                nextKey = if (fetchResponse.hasNext) page + 1 else null,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    abstract suspend fun onFetchPage(pageNumber: Int): PagedResult<T>

    companion object {
        const val FIRST_PAGE = 0
    }
}

fun <T : Any, R : Any> createPagingSource(
    mapper: (T) -> R,
    config: PagingConfig = PagingConfig(),
    block: suspend (pageNumber: Int) -> PagedResult<T>
): Pager<Int, R> {
    return Pager(
        config = config,
        pagingSourceFactory = {
            object : BasePagingSource<R>() {
                override suspend fun onFetchPage(pageNumber: Int): PagedResult<R> {
                    val result = block(pageNumber)
                    return PagedResult(
                        items = result.items.map(mapper),
                        hasNext = result.hasNext,
                        hasPrevious = result.hasPrevious,
                        currentPage = result.currentPage,
                        totalPages = result.totalPages,
                        totalItems = result.totalItems
                    )
                }
            }
        }
    )
}
