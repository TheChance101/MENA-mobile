package net.thechance.mena.dukan.presentation.util.pagination.base

import net.thechance.mena.dukan.presentation.util.pagination.PagedFetchResponse
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

    abstract suspend fun onFetchPage(pageNumber: Int): PagedFetchResponse<T>

    companion object {
        const val FIRST_PAGE = 1
    }
}

fun <T : Any> createPagingSource(
    config: PagingConfig = PagingConfig(),
    block: suspend (pageNumber: Int) -> PagedFetchResponse<T>
): Pager<Int, T> {
    return Pager(
        config = config,
        pagingSourceFactory = {
            object : BasePagingSource<T>() {
                override suspend fun onFetchPage(pageNumber: Int): PagedFetchResponse<T> {
                    return block(pageNumber)
                }
            }
        }
    )
}
