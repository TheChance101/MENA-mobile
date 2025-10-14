package net.thechance.mena.dukan.presentation.util.stubPreviews


import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.util.pagination.PagingConfig
import net.thechance.mena.dukan.presentation.util.pagination.PagingSource

class FakePagingSource<Key : Any, Value : Any>(
    private val items: List<Value>
) : PagingSource<Key, Value>() {

    override suspend fun load(params: LoadParams<Key>): LoadResult<Key, Value> {
        return LoadResult.Page(
            data = items,
            prevKey = null,
            nextKey = null
        )
    }
}

fun <Key : Any, Value : Any> createFakePager(
    items: List<Value>
): Pager<Key, Value> {
    return Pager(
        config = PagingConfig(pageSize = items.size),
        pagingSourceFactory = { FakePagingSource(items) }
    )
}