package net.thechance.mena.dukan.presentation.util.pagination.base

import androidx.paging.PagingSource
import androidx.paging.PagingState

internal class BasePagationSource<T : Any>(
    private val onError: (Exception) -> Unit = {},
    private val onFetchPage: suspend (pageNumber: Int, pageSize: Int) -> List<T>
) : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>) = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val page = params.key ?: 0
            val fetchResponse = onFetchPage(page, PAGE_SIZE)
            val isFirstPage = page == 0
            val isLastPage = fetchResponse.size < PAGE_SIZE

            LoadResult.Page(
                data = fetchResponse,
                prevKey = if (isFirstPage) null else page.minus(1),
                nextKey = if (isLastPage) null else page.plus(1),
            )
        } catch (e: Exception) {
            onError(e)
            LoadResult.Error(e)
        }
    }

    companion object {
        const val PAGE_SIZE = 10
    }
}