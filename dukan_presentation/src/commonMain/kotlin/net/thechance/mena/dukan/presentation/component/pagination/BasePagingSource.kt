package net.thechance.mena.dukan.presentation.component.pagination

abstract class BasePagingSource<T : Any> : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingData<T>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val page = params.key ?: FIRST_PAGE
            val fetchResponse = onFetchPage(pageNumber = page)
            val isFirstPage = fetchResponse.currentPage == FIRST_PAGE
            val isLastPage = fetchResponse.totalPages == page || fetchResponse.items.isEmpty()

            LoadResult.Page(
                data = fetchResponse.items,
                prevKey = if (isFirstPage) null else page.minus(1),
                nextKey = if (isLastPage) null else page.plus(1),
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
    block: suspend (pageNumber: Int) -> PagedFetchResponse<T>
): Pager<Int, T> {
    return Pager(
        pagingSourceFactory = {
            object : BasePagingSource<T>() {
                override suspend fun onFetchPage(pageNumber: Int): PagedFetchResponse<T> {
                    return block(pageNumber)
                }
            }
        }
    )
}
