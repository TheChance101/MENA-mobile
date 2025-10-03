package net.thechance.mena.wallet.presentation.utils

class Paginator<Item>(
    private val onRequest: suspend (nextKey: Int, pageSize: Int) -> List<Item>,
    private val onError: (Throwable) -> Unit,
    private val onSuccess: (items: List<Item>) -> Unit,
    private val onLoadUpdated: (Boolean) -> Unit,
    private val pageSize: Int
) {
    private var currentPage = 1
    private var isRequest = false
    private var endPages = false

    suspend fun loadNextItems() {
        if (isRequest || endPages) return

        try {
            isRequest = true
            onLoadUpdated(true)
            val items = onRequest(currentPage, pageSize)
            if (items.isEmpty() || items.size < pageSize) {
                endPages = true
            }
            val nextPage = currentPage + 1
            onSuccess(items)
            currentPage = nextPage
            onLoadUpdated(false)
        } catch (error: Throwable) {
            onLoadUpdated(false)
            throw error
        } finally {
            isRequest = false
        }
    }
}