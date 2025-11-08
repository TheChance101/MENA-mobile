package net.thechance.mena.faith.data.utils

suspend fun <T> loadFromCacheOrFetch(
    cacheBlock: (suspend () -> T?)? = null,
    networkBlock: suspend () -> T,
    syncBlock: (suspend (T) -> Unit)? = null,
): T = runCatching { cacheBlock?.invoke() }.getNotNullOrElse {
    networkBlock().also {
        syncBlock?.invoke(it)
    }
}.onFailure { it }.getOrThrow()

private suspend fun <T> Result<T?>.getNotNullOrElse(elseBlock: suspend () -> T): Result<T> =
    runCatching { getOrElse { elseBlock() } ?: elseBlock() }