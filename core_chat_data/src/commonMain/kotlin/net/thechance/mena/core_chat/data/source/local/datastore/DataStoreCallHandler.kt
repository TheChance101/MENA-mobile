package net.thechance.mena.core_chat.data.source.local.datastore

import net.thechance.mena.core_chat.domain.exception.ChatException
import net.thechance.mena.core_chat.domain.exception.UnknownException

suspend fun <T> tryCall(
    defaultException: ChatException = UnknownException("Unknown error occurred"),
    block: suspend () -> T
): T {
    return try {
        block()
    } catch (e: Exception) {
        throw defaultException
    }
}