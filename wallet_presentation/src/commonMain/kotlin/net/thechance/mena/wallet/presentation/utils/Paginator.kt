package net.thechance.mena.wallet.presentation.utils

class Paginator<Item>(
    private val onRequest: suspend (nextKey: Int) -> Result<List<Item>>,
    private val onError: (Throwable) -> Unit,
    private val onSuccess: (items: List<Item>) -> Unit,
    private val onLoadUpdated: (Boolean) -> Unit
    ) {
    private var currentPage = 1
    private var isRequest = false
    private var endPages = false

    suspend fun loadNextItems() {
        if (isRequest || endPages) return

        try {
            isRequest = true
            onLoadUpdated(true)
            val items = onRequest(currentPage)
            onLoadUpdated(false)

            items.onSuccess { items ->
                if (items.isEmpty()) {
                    endPages = true
                }
                val nextPage = currentPage + 1
                onSuccess(items)
                currentPage = nextPage
            }.onFailure { error ->
                onError(error)
            }

        } finally {
            isRequest = false
        }
    }
}