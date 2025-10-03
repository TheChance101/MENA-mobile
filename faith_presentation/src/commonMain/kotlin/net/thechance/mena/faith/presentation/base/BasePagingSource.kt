package net.thechance.mena.faith.presentation.base

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.Flow

internal class BasePagingSource<T : Any>(
    private val onError: (Throwable) -> Unit = {},
    private val onFetchPage: suspend (pageNumber: Int, pageSize: Int) -> List<T>
) : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>) = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val page = params.key ?: 0
            val fetchResponse = onFetchPage(page, PAGING_PAGE_SIZE)
            val isFirstPage = page == 0
            val isLastPage = fetchResponse.size < PAGING_PAGE_SIZE

            LoadResult.Page(
                data = fetchResponse,
                prevKey = if (isFirstPage) null else page.minus(1),
                nextKey = if (isLastPage) null else page.plus(1),
            )
        } catch (e: Throwable) {
            onError(e)
            LoadResult.Error(e)
        }
    }

    companion object {
        const val PAGING_PAGE_SIZE = 10
    }
}

fun <T : Any> createPagingSourceFlow(
    onError: (Throwable) -> Unit = {},
    block: suspend (pageNumber: Int, pageSize: Int) -> List<T>
): Flow<PagingData<T>> {
    return Pager(
        config = PagingConfig(
            pageSize = BasePagingSource.PAGING_PAGE_SIZE,
            initialLoadSize = BasePagingSource.PAGING_PAGE_SIZE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            BasePagingSource(onError = onError, onFetchPage = block)
        }
    ).flow
}
