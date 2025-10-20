package net.thechance.mena.core_chat.data.source.local.datastore

import net.thechance.mena.core_chat.domain.exception.ChatException

suspend fun <T> tryCall(
    defaultException: (Throwable) -> ChatException,
    block: suspend () -> T
): T {
    return try {
        block()
    } catch (e: Exception) {
        throw defaultException(e)
    }
}