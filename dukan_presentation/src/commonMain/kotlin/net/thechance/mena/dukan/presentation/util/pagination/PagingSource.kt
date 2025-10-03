package net.thechance.mena.dukan.presentation.util.pagination

abstract class PagingSource<Key : Any, Value : Any> {

    data class LoadParams<Key : Any>(
        val key: Key?,
        val loadSize: Int
    )

    sealed class LoadResult<Key : Any, Value : Any> {
        data class Page<Key : Any, Value : Any>(
            val data: List<Value>,
            val prevKey: Key?,
            val nextKey: Key?
        ) : LoadResult<Key, Value>()

        data class Error<Key : Any, Value : Any>(
            val throwable: Exception
        ) : LoadResult<Key, Value>()
    }

    abstract suspend fun load(params: LoadParams<Key>): LoadResult<Key, Value>
}