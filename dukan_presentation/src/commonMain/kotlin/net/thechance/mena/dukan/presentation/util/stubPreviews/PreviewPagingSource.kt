package net.thechance.mena.dukan.presentation.util.stubPreviews


import net.thechance.mena.dukan.presentation.util.pagination.PagerOld
import net.thechance.mena.dukan.presentation.util.pagination.PagingConfigOld
import net.thechance.mena.dukan.presentation.util.pagination.PagingSourceOld

class FakePagingSourceOld<Key : Any, Value : Any>(
    private val items: List<Value>
) : PagingSourceOld<Key, Value>() {

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
): PagerOld<Key, Value> {
    return PagerOld(
        config = PagingConfigOld(pageSize = items.size),
        pagingSourceOldFactory = { FakePagingSourceOld(items) }
    )
}