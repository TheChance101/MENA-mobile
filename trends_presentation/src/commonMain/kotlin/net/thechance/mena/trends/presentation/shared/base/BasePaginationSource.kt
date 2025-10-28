package net.thechance.mena.trends.presentation.shared.base

import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import net.thechance.mena.trends.domain.exception.NoInternetException

internal class BasePagingSource<T : Any>(
    private val fetch: suspend (Int) -> List<T>
) : PagingSource<Int, T>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val nextPage = params.key ?: 0
            val result = fetch(nextPage)
            LoadResult.Page(
                data = result,
                prevKey = if (nextPage == 0) null else nextPage - 1,
                nextKey = if (result.size < 10) null else nextPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, T>) = state.anchorPosition
}

fun <T : Any> createPager(
    scope: CoroutineScope,
    pageSize: Int = 10,
    prefetchDistance: Int = 5,
    initialLoadSize: Int = 10,
    loadPage: suspend (page: Int) -> List<T>
): Flow<PagingData<T>> {
    return Pager(
        config = PagingConfig(
            pageSize = pageSize,
            prefetchDistance = prefetchDistance,
            initialLoadSize = initialLoadSize
        ),
        pagingSourceFactory = { BasePagingSource(loadPage) }
    ).flow.cachedIn(scope)
}

fun LoadState.toErrorState(): ErrorState? {
    return when (this) {
        is LoadState.Error -> {
            when (val exception = this.error) {
                is NoInternetException -> ErrorState.NoInternet
                else -> ErrorState.RequestFailed(exception.message)
            }
        }
        else -> null
    }
}