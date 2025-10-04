package net.thechance.mena.wallet.presentation.utils

class Paginator<Key, Items>(
    private val initialKey: Key,
    private val onLoadUpdated: (Boolean) -> Unit,
    private val onRequest: suspend (nextKey: Key) -> Items,
    private val getNextKey: suspend (currentKey: Key, result: Items) -> Key,
    private val onError: suspend (Throwable?) -> Unit,
    private val onSuccess: suspend (result: Items, newKey: Key) -> Unit,
    private val endReached: (currentKey: Key, result: Items) -> Boolean
) {

    private var currentKey = initialKey
    private var isMakingRequest = false
    private var isEndReached = false

    suspend fun loadNextItems() {
        if(isMakingRequest || isEndReached) {
            return
        }

        isMakingRequest = true
        onLoadUpdated(true)

        runCatching {
            onRequest(currentKey)
        }.also {
            isMakingRequest = false
        }.onSuccess { items ->
            currentKey = getNextKey(currentKey, items)
            onSuccess(items, currentKey)
            onLoadUpdated(false)
            isEndReached = endReached(currentKey, items)
        }.onFailure {
            onError(it)
            onLoadUpdated(false)
            return
        }
    }

    fun reset() {
        currentKey = initialKey
        isEndReached = false
    }
}