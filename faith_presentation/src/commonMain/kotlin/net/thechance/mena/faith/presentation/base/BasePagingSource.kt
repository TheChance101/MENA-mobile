package net.thechance.mena.faith.presentation.base

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.Flow
import net.thechance.mena.faith.domain.entity.PagedFetchResponse

abstract class BasePagingSource<T : Any> : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val page = params.key ?: 0
            val fetchResponse = onFetchPage(pageNumber = page)
            val isFirstPage = fetchResponse.currentPage == 0
            val isLastPage = fetchResponse.totalPages == page
            val isEmptyPage = fetchResponse.items.isEmpty()

            LoadResult.Page(
                data = fetchResponse.items,
                prevKey = if (isFirstPage) null else page.minus(1),
                nextKey = if (isLastPage || isEmptyPage) null else page.plus(1),
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    abstract suspend fun onFetchPage(pageNumber: Int): PagedFetchResponse<T>

    companion object {
        const val PAGING_PAGE_SIZE = 10
    }
}

fun <T : Any> createPagingSourceFlow(
    query: String = "",
    block: suspend (query: String, pageNumber: Int) -> PagedFetchResponse<T>
): Flow<PagingData<T>> {
    return Pager(
        config = PagingConfig(
            pageSize = BasePagingSource.PAGING_PAGE_SIZE
        ),
        pagingSourceFactory = {
            object : BasePagingSource<T>() {
                override suspend fun onFetchPage(pageNumber: Int): PagedFetchResponse<T> {
                    return block(query, pageNumber)
                }
            }
        }
    ).flow
}