package net.thechance.mena.dukan.presentation.util.pagination

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PagerOld<Key : Any, Value : Any>(
    private val config: PagingConfigOld,
    private val pagingSourceOldFactory: () -> PagingSourceOld<Key, Value>
) {
    private val _flow = MutableStateFlow(PagingDataOld<Value>())
    val flow: Flow<PagingDataOld<Value>> = _flow.asStateFlow()

    private var currentPagingSourceOld: PagingSourceOld<Key, Value>? = null
    private var currentKey: Key? = null
    private var loadedItemsCount = 0
    private val loadMutex = Mutex()

    suspend fun load(key: Key? = null) {

        loadMutex.withLock {

            if (_flow.value.isLoading) return

            startLoading()
            try {
                val pagingSource = setPagingSource()
                val params = setParams(key)
                handleLoadResult(pagingSource.load(params), key)

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

    private fun setErrorState(throwable: Exception) {
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
            isRefreshing = false,
        )
    }


    private fun handleLoadResult(
        result: PagingSourceOld.LoadResult<Key, Value>,
        key: Key?
    ) {
        when (result) {
            is PagingSourceOld.LoadResult.Page -> setPageResult(result, key)
            is PagingSourceOld.LoadResult.Error -> setErrorState(result.throwable)
        }
    }

    private fun setPageResult(
        result: PagingSourceOld.LoadResult.Page<Key, Value>,
        key: Key?
    ) {
        val isRefresh = key == null && currentKey == null
        val newItems = if (isRefresh) result.data else _flow.value.items + result.data

        val finalItems = limitMaxSize(newItems)

        currentKey = result.nextKey
        loadedItemsCount = finalItems.size

        setSuccessState(finalItems, result.nextKey)
    }

    private fun limitMaxSize(items: List<Value>): List<Value> {
        return if (config.maxSize != Int.MAX_VALUE && items.size > config.maxSize)
            items.takeLast(config.maxSize)
        else items
    }

    private fun startLoading() {
        _flow.value = _flow.value.copy(isLoading = true, error = null)
    }

    private fun setPagingSource(): PagingSourceOld<Key, Value> {
        return currentPagingSourceOld ?: pagingSourceOldFactory().also {
            currentPagingSourceOld = it
        }
    }

    private fun setParams(key: Key?): PagingSourceOld.LoadParams<Key> {
        return PagingSourceOld.LoadParams(
            key = key ?: currentKey,
            loadSize = config.pageSize
        )
    }
}
