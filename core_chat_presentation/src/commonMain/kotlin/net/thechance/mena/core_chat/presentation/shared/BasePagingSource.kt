package net.thechance.mena.core_chat.presentation.shared

import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.thechance.mena.core_chat.domain.exception.ChatException
import net.thechance.mena.core_chat.domain.exception.UnknownException
import net.thechance.mena.core_chat.domain.model.PagedData

class BasePagingSource<T : Any>(
    private val onError: ((ChatException) -> Unit)? = null,
    private val fetchItems: suspend (page: Int) -> PagedData<T>
) : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: STARTING_PAGE_INDEX

        return try {
            val pagedData = fetchItems(page)
            LoadResult.Page(
                data = pagedData.data,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page.minus(1),
                nextKey = if (pagedData.isLastPage) null else page.plus(1)
            )
        } catch (e: Exception) {
            onError?.invoke(
                when (e) {
                    is ChatException -> e
                    else -> UnknownException(e.message)
                }
            )
            LoadResult.Error(e)
        }
    }

    companion object {
        private const val STARTING_PAGE_INDEX = 0
    }
}