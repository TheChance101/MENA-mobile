package net.thechance.mena.dukan.presentation.util.pagination

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class Pager<Key : Any, Value : Any>(
    private val config: PagingConfig,
    private val pagingSourceFactory: () -> PagingSource<Key, Value>
) {
    private val _flow = MutableStateFlow(PagingData<Value>())
    val flow: Flow<PagingData<Value>> = _flow.asStateFlow()

    private var currentPagingSource: PagingSource<Key, Value>? = null
    private var currentKey: Key? = null
    private var loadedItemsCount = 0
    private val loadMutex = Mutex()

    suspend fun load(key: Key? = null) {

        loadMutex.withLock {
            if (_flow.value.isLoading) return

            _flow.value = _flow.value.copy(isLoading = true, error = null)

            try {
                val pagingSource = currentPagingSource ?: pagingSourceFactory().also {
                    currentPagingSource = it
                }


                val params = PagingSource.LoadParams(
                    key = key ?: currentKey,
                    loadSize = config.pageSize
                )

                when (val result = pagingSource.load(params)) {
                    is PagingSource.LoadResult.Page -> {
                        val isRefresh = key == null && currentKey == null
                        val newItems = if (isRefresh) result.data
                        else _flow.value.items + result.data

                        val finalItems =
                            if (config.maxSize != Int.MAX_VALUE && newItems.size > config.maxSize) {
                                newItems.takeLast(config.maxSize)
                            } else {
                                newItems
                            }

                        currentKey = result.nextKey
                        loadedItemsCount = finalItems.size

                        setSuccessState(finalItems, result.nextKey)
                    }
                    is PagingSource.LoadResult.Error -> {
                        setErrorState(result.throwable)
                    }
                }
            } catch (e: Exception) {
                setErrorState(e)
            }
        }
    }

    fun isShouldLoadMore(lastVisibleIndex: Int, totalItems: Int): Boolean {
        val remainingItems = totalItems - lastVisibleIndex
        return remainingItems <= config.prefetchDistance &&
                _flow.value.hasMore &&
                _flow.value.isLoading.not()
    }

    suspend fun refresh() {
        _flow.value = _flow.value.copy(isRefreshing = true)
        currentKey = null
        loadedItemsCount = 0
        load()
    }

    private fun setErrorState(throwable: Throwable) {
        _flow.value = _flow.value.copy(
            isLoading = false,
            error = throwable,
            isRefreshing = false
        )
    }

    private fun setSuccessState(
        finalItems: List<Value>,
        nextKey: Key?
    ) {
        _flow.value = _flow.value.copy(
            items = finalItems,
            isLoading = false,
            error = null,
            hasMore = nextKey != null,
            isRefreshing = false
        )
    }
}